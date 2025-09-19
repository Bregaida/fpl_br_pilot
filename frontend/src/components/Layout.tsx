import React from 'react'
import { Navbar } from './Navbar'

export function Layout({ children }: { readonly children: React.ReactNode }) {
  return (
    <div className="min-h-screen bg-background">
      <Navbar />
      <main className="max-w-7xl mx-auto px-4 py-6 md:py-8 pb-20 md:pb-8">
        <div className="fade-in">
          {children}
        </div>
      </main>
    </div>
  )
}
