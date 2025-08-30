"use client"

import { useState } from "react"
import { useRouter } from "next/navigation"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { toast } from "@/components/ui/use-toast"
import { FplForm, type FplFormValues } from "@/features/fpl/components/fpl-form"
import { FplSimplifiedForm, type FplSimplifiedFormValues } from "@/features/fpl/components/fpl-simplified-form"
import { 
  useGenerateFplPreview, 
  useGenerateSimplifiedFplPreview,
  useSubmitFpl,
  useSubmitSimplifiedFpl,
  useRecentFpls
} from "@/features/fpl/hooks/use-fpl-queries"

export default function FplPage() {
  const router = useRouter()
  const [activeTab, setActiveTab] = useState("complete")
  const [fplPreview, setFplPreview] = useState<string | null>(null)
  const [isSubmitting, setIsSubmitting] = useState(false)
  
  // API hooks
  const { mutateAsync: generatePreview } = useGenerateFplPreview()
  const { mutateAsync: generateSimplifiedPreview } = useGenerateSimplifiedFplPreview()
  const { mutateAsync: submitFpl } = useSubmitFpl()
  const { mutateAsync: submitSimplifiedFpl } = useSubmitSimplifiedFpl()
  const { data: recentFpls, isLoading: isLoadingRecent } = useRecentFpls()

  const handleGeneratePreview = async (data: FplFormValues | FplSimplifiedFormValues, isSimplified = false) => {
    try {
      setIsSubmitting(true)
      
      let response
      if (isSimplified) {
        response = await generateSimplifiedPreview(data as FplSimplifiedFormValues)
      } else {
        response = await generatePreview(data as FplFormValues)
      }
      
      setFplPreview(response.mensagemFpl)
      
      toast({
        title: "Pré-visualização gerada",
        description: "O plano de voo foi gerado com sucesso.",
      })
    } catch (error) {
      console.error("Error generating FPL:", error)
      toast({
        title: "Erro",
        description: "Não foi possível gerar o plano de voo. Tente novamente.",
        variant: "destructive",
      })
    } finally {
      setIsSubmitting(false)
    }
  }
  
  const handleSubmitFpl = async (data: FplFormValues | FplSimplifiedFormValues, isSimplified = false) => {
    try {
      setIsSubmitting(true)
      
      if (isSimplified) {
        await submitSimplifiedFpl(data as FplSimplifiedFormValues)
      } else {
        await submitFpl(data as FplFormValues)
      }
      
      toast({
        title: "Sucesso",
        description: "Plano de voo enviado com sucesso!",
      })
      
      // Redirect to the FPL list or show success message
      router.push("/fpl/list")
    } catch (error) {
      console.error("Error submitting FPL:", error)
      toast({
        title: "Erro",
        description: "Não foi possível enviar o plano de voo. Tente novamente.",
        variant: "destructive",
      })
    } finally {
      setIsSubmitting(false)
    }
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h1 className="text-3xl font-bold tracking-tight">Plano de Voo</h1>
      </div>

      <Tabs 
        defaultValue="complete" 
        className="space-y-4"
        onValueChange={setActiveTab}
      >
        <TabsList>
          <TabsTrigger value="complete">Completo</TabsTrigger>
          <TabsTrigger value="simplified">Simplificado</TabsTrigger>
        </TabsList>

        <TabsContent value="complete" className="space-y-4">
          <Card>
            <CardHeader>
              <CardTitle>Formulário Completo</CardTitle>
            </CardHeader>
            <CardContent>
              <FplForm 
                onSubmit={(data) => handleGeneratePreview(data, false)}
                isSubmitting={isSubmitting}
                onGeneratePreview={(data) => handleGeneratePreview(data, false)}
                onSubmitFpl={(data) => handleSubmitFpl(data, false)}
              />
            </CardContent>
          </Card>
        </TabsContent>

        <TabsContent value="simplified" className="space-y-4">
          <Card>
            <CardHeader>
              <CardTitle>Formulário Simplificado</CardTitle>
            </CardHeader>
            <CardContent>
              <FplSimplifiedForm 
                onSubmit={(data) => handleGeneratePreview(data, true)}
                isSubmitting={isSubmitting}
                onGeneratePreview={(data) => handleGeneratePreview(data, true)}
                onSubmitFpl={(data) => handleSubmitFpl(data, true)}
              />
            </CardContent>
          </Card>
        </TabsContent>
      </Tabs>

      {fplPreview && (
        <Card>
          <CardHeader>
            <CardTitle>Pré-visualização da Mensagem ATS</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="p-4 bg-muted rounded-md font-mono text-sm">
              {fplPreview}
            </div>
            <div className="mt-4 flex gap-2">
              <Button
                variant="outline"
                onClick={() => {
                  navigator.clipboard.writeText(fplPreview)
                  toast({
                    title: "Copiado!",
                    description: "A mensagem foi copiada para a área de transferência.",
                  })
                }}
                disabled={isSubmitting}
              >
                Copiar Mensagem
              </Button>
              <Button
                onClick={() => handleSubmitFpl(fplPreview as any, activeTab === 'simplified')}
                disabled={isSubmitting}
              >
                {isSubmitting ? "Enviando..." : "Enviar Plano de Voo"}
              </Button>
            </div>
          </CardContent>
        </Card>
      )}
    </div>
  )
}
