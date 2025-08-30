import { render, screen, fireEvent } from '@testing-library/react'
import { DataTable } from '../data-table'
import { ColumnDef } from '@tanstack/react-table'
import { vi } from 'vitest'

// Mock data
type Person = {
  id: number
  name: string
  age: number
  email: string
}

const data: Person[] = [
  { id: 1, name: 'John Doe', age: 30, email: 'john@example.com' },
  { id: 2, name: 'Jane Smith', age: 25, email: 'jane@example.com' },
  { id: 3, name: 'Bob Johnson', age: 40, email: 'bob@example.com' },
]

const columns: ColumnDef<Person>[] = [
  {
    accessorKey: 'name',
    header: 'Name',
  },
  {
    accessorKey: 'age',
    header: 'Age',
  },
  {
    accessorKey: 'email',
    header: 'Email',
  },
]

describe('DataTable', () => {
  it('renders the table with data', () => {
    render(
      <DataTable
        columns={columns}
        data={data}
        pageSize={10}
        pageCount={1}
      />
    )

    // Check if headers are rendered
    expect(screen.getByText('Name')).toBeInTheDocument()
    expect(screen.getByText('Age')).toBeInTheDocument()
    expect(screen.getByText('Email')).toBeInTheDocument()

    // Check if data is rendered
    expect(screen.getByText('John Doe')).toBeInTheDocument()
    expect(screen.getByText('john@example.com')).toBeInTheDocument()
    expect(screen.getByText('30')).toBeInTheDocument()
  })

  it('handles pagination', () => {
    const onPageChange = vi.fn()
    
    render(
      <DataTable
        columns={columns}
        data={data}
        pageSize={1}
        pageCount={3}
        onPageChange={onPageChange}
        showPagination={true}
      />
    )

    // Check if pagination controls are rendered
    const nextButton = screen.getByRole('button', { name: /go to next page/i })
    expect(nextButton).toBeInTheDocument()
    
    // Simulate page change
    fireEvent.click(nextButton)
    expect(onPageChange).toHaveBeenCalledWith(1)
  })

  it('handles search', () => {
    render(
      <DataTable
        columns={columns}
        data={data}
        pageSize={10}
        pageCount={1}
        searchKey="name"
        searchPlaceholder="Search by name..."
      />
    )

    // Check if search input is rendered
    const searchInput = screen.getByPlaceholderText('Search by name...')
    expect(searchInput).toBeInTheDocument()
    
    // Simulate search
    fireEvent.change(searchInput, { target: { value: 'John' } })
    
    // Check if only matching rows are shown
    expect(screen.getByText('John Doe')).toBeInTheDocument()
    expect(screen.queryByText('Jane Smith')).not.toBeInTheDocument()
  })

  it('shows empty state when no data', () => {
    render(
      <DataTable
        columns={columns}
        data={[]}
        pageSize={10}
        pageCount={0}
        emptyMessage="No data available"
      />
    )

    // Check if empty state is shown
    expect(screen.getByText('No data available')).toBeInTheDocument()
  })

  it('handles row selection', () => {
    const onRowSelectionChange = vi.fn()
    
    render(
      <DataTable
        columns={columns}
        data={data}
        pageSize={10}
        pageCount={1}
        enableRowSelection={true}
        onRowSelectionChange={onRowSelectionChange}
      />
    )

    // Check if select all checkbox is rendered
    const selectAllCheckbox = screen.getByLabelText('Select all')
    expect(selectAllCheckbox).toBeInTheDocument()
    
    // Simulate row selection
    const firstRowCheckbox = screen.getAllByRole('checkbox')[1] // First checkbox is select all
    fireEvent.click(firstRowCheckbox)
    
    expect(onRowSelectionChange).toHaveBeenCalledWith([data[0]])
  })
})
