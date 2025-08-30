import { Button } from "@/components/ui/button"
import { render, screen } from "@testing-library/react"

describe("Path Alias Test", () => {
  it("should import Button component using @/ alias", () => {
    render(<Button>Test Button</Button>)
    const button = screen.getByText(/test button/i)
    expect(button).toBeInTheDocument()
  })
})
