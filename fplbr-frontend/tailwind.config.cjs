/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        brand: {
          DEFAULT: '#0B1220',
          50: '#1E3A8A',
          600: '#1E3A8A',
        },
        accent: '#06B6D4',
        success: '#10B981',
        warn: '#F59E0B',
        danger: '#EF4444',
        surface: '#FFFFFF',
        bg: '#F1F5F9',
        muted: '#64748B',
        primary: {
          50: '#f0f9ff',
          100: '#e0f2fe',
          200: '#bae6fd',
          300: '#7dd3fc',
          400: '#38bdf8',
          500: '#0ea5e9',
          600: '#0284c7',
          700: '#0369a1',
          800: '#075985',
          900: '#0c4a6e',
        },
      },
    },
  },
  plugins: [],
}
