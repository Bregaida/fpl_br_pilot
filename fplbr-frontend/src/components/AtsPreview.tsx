import { useState } from 'react';
import { DocumentDuplicateIcon, CheckIcon } from '@heroicons/react/24/outline';

interface AtsPreviewProps {
  content: string;
  isLoading?: boolean;
}

export default function AtsPreview({ content, isLoading = false }: AtsPreviewProps) {
  const [copied, setCopied] = useState(false);

  const handleCopy = async () => {
    try {
      await navigator.clipboard.writeText(content);
      setCopied(true);
      setTimeout(() => setCopied(false), 2000);
    } catch (err) {
      console.error('Failed to copy text: ', err);
    }
  };

  return (
    <div className="bg-white rounded-lg shadow p-4">
      <div className="flex justify-between items-center mb-2">
        <h3 className="text-lg font-semibold">Pré-visualização ATS</h3>
        <button
          onClick={handleCopy}
          disabled={!content || isLoading}
          className="inline-flex items-center px-3 py-1.5 border border-transparent text-xs font-medium rounded-md shadow-sm text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 disabled:opacity-50 disabled:cursor-not-allowed"
          title="Copiar para a área de transferÃªncia"
        >
          {copied ? (
            <>
              <CheckIcon className="h-4 w-4 mr-1" aria-hidden="true" />
              Copiado!
            </>
          ) : (
            <>
              <DocumentDuplicateIcon className="h-4 w-4 mr-1" aria-hidden="true" />
              Copiar
            </>
          )}
        </button>
      </div>
      
      {isLoading ? (
        <div className="animate-pulse bg-gray-100 rounded h-24 flex items-center justify-center">
          <p className="text-gray-500">Gerando pré-visualização...</p>
        </div>
      ) : content ? (
        <pre className="bg-gray-50 p-3 rounded text-sm font-mono whitespace-pre-wrap overflow-auto max-h-96">
          {content}
        </pre>
      ) : (
        <div className="bg-gray-50 p-4 rounded text-sm text-gray-500 italic">
          Preencha o formulário para visualizar a mensagem ATS
        </div>
      )}
    </div>
  );
}
