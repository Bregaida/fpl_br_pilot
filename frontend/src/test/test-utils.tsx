import React, { ReactNode } from 'react';
import { render, RenderOptions } from '@testing-library/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { MemoryRouter, MemoryRouterProps } from 'react-router-dom';
import { ThemeProvider } from '@/components/theme-provider';

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      retry: false,
    },
  },
  logger: {
    log: console.log,
    warn: console.warn,
    error: () => {},
  },
});

type WrapperProps = {
  children: ReactNode;
  initialEntries?: MemoryRouterProps['initialEntries'];
};

const AllTheProviders = ({ children, initialEntries = ['/'] }: WrapperProps) => {
  return (
    <QueryClientProvider client={queryClient}>
      <ThemeProvider defaultTheme="light" storageKey="vite-ui-theme">
        <MemoryRouter initialEntries={initialEntries}>
          {children}
        </MemoryRouter>
      </ThemeProvider>
    </QueryClientProvider>
  );
};

const customRender = (
  ui: React.ReactElement,
  options?: Omit<RenderOptions, 'wrapper'> & { initialEntries?: MemoryRouterProps['initialEntries'] }
) => {
  const { initialEntries, ...rest } = options || {};
  return render(ui, {
    wrapper: ({ children }) => (
      <AllTheProviders initialEntries={initialEntries}>
        {children}
      </AllTheProviders>
    ),
    ...rest,
  });
};

export * from '@testing-library/react';
export { customRender as render };

// Re-export userEvent from @testing-library/user-event with setup
import userEvent from '@testing-library/user-event';
export { userEvent };
