/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        // Vermelho para aviação (inspirado em sinais de perigo e alertas)
        aviation: {
          red: {
            50: '#fef2f2',
            100: '#fee2e2',
            200: '#fecaca',
            300: '#fca5a5',
            400: '#f87171',
            500: '#ef4444',
            600: '#dc2626',  // Vermelho principal
            700: '#b91c1c',
            800: '#991b1b',
            900: '#7f1d1d',
          },
          // Azul petróleo (inspirado em uniformes de pilotos e instrumentos)
          navy: {
            50: '#f8fafc',
            100: '#f1f5f9',
            200: '#e2e8f0',
            300: '#cbd5e1',
            400: '#94a3b8',
            500: '#64748b',
            600: '#475569',
            700: '#334155',
            800: '#1e293b',  // Azul petróleo principal
            900: '#0f172a',
          },
          // Branco puro para contraste
          white: '#ffffff',
          // Cinza para formulários e elementos secundários
          gray: {
            50: '#f9fafb',
            100: '#f3f4f6',
            200: '#e5e7eb',
            300: '#d1d5db',
            400: '#9ca3af',
            500: '#6b7280',
            600: '#4b5563',
            700: '#374151',
            800: '#1f2937',
            900: '#111827',
          },
        },
        // Cores do sistema
        brand: {
          DEFAULT: '#dc2626', // Vermelho aviação
          light: '#f87171',   // Vermelho claro
          dark: '#991b1b',    // Vermelho escuro
        },
        accent: '#1e293b',    // Azul petróleo
        success: '#10b981',
        warning: '#f59e0b',
        danger: '#ef4444',
        surface: '#ffffff',
        background: '#f9fafb',
        muted: '#6b7280',
      },
      fontFamily: {
        sans: ['Inter', 'system-ui', 'sans-serif'],
        display: ['Poppins', 'system-ui', 'sans-serif'],
      },
      backgroundImage: {
        'gradient-aviation-red': 'linear-gradient(135deg, #dc2626 0%, #991b1b 100%)',
        'gradient-aviation-navy': 'linear-gradient(135deg, #1e293b 0%, #0f172a 100%)',
        'gradient-aviation-mixed': 'linear-gradient(135deg, #dc2626 0%, #1e293b 50%, #991b1b 100%)',
        'gradient-cockpit': 'linear-gradient(135deg, #1e293b 0%, #334155 50%, #475569 100%)',
        'gradient-instrument': 'linear-gradient(180deg, #f8fafc 0%, #e2e8f0 50%, #cbd5e1 100%)',
        'gradient-warning': 'linear-gradient(135deg, #fef2f2 0%, #fee2e2 100%)',
        'gradient-runway': 'linear-gradient(90deg, #ffffff 0%, #f3f4f6 50%, #ffffff 100%)',
      },
      boxShadow: {
        'soft': '0 2px 15px -3px rgba(0, 0, 0, 0.07), 0 10px 20px -2px rgba(0, 0, 0, 0.04)',
        'soft-lg': '0 10px 25px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -2px rgba(0, 0, 0, 0.05)',
      },
      animation: {
        'fade-in': 'fadeIn 0.5s ease-in-out',
        'slide-up': 'slideUp 0.3s ease-out',
        'bounce-soft': 'bounceSoft 0.6s ease-in-out',
        'fly-in': 'flyIn 0.8s ease-out',
        'takeoff': 'takeoff 1.2s ease-in-out',
        'landing': 'landing 0.6s ease-in-out',
        'radar-sweep': 'radarSweep 3s linear infinite',
        'propeller': 'propeller 0.1s linear infinite',
        'altitude': 'altitude 2s ease-in-out infinite',
      },
      keyframes: {
        fadeIn: {
          '0%': { opacity: '0' },
          '100%': { opacity: '1' },
        },
        slideUp: {
          '0%': { transform: 'translateY(10px)', opacity: '0' },
          '100%': { transform: 'translateY(0)', opacity: '1' },
        },
        bounceSoft: {
          '0%, 100%': { transform: 'translateY(0)' },
          '50%': { transform: 'translateY(-5px)' },
        },
        flyIn: {
          '0%': { transform: 'translateX(-100px) translateY(20px)', opacity: '0' },
          '100%': { transform: 'translateX(0) translateY(0)', opacity: '1' },
        },
        takeoff: {
          '0%': { transform: 'translateY(0) scale(1)', opacity: '1' },
          '50%': { transform: 'translateY(-20px) scale(1.05)', opacity: '0.8' },
          '100%': { transform: 'translateY(-40px) scale(1.1)', opacity: '0.6' },
        },
        landing: {
          '0%': { transform: 'translateY(-20px) scale(1.1)', opacity: '0.6' },
          '100%': { transform: 'translateY(0) scale(1)', opacity: '1' },
        },
        radarSweep: {
          '0%': { transform: 'rotate(0deg)' },
          '100%': { transform: 'rotate(360deg)' },
        },
        propeller: {
          '0%': { transform: 'rotate(0deg)' },
          '100%': { transform: 'rotate(360deg)' },
        },
        altitude: {
          '0%, 100%': { transform: 'translateY(0)' },
          '50%': { transform: 'translateY(-10px)' },
        },
      },
    },
  },
  plugins: [],
}
