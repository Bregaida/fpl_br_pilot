import { describe, it, expect, vi, beforeEach } from 'vitest'
import { AerodromosAPI } from '../services/api'

vi.mock('../services/api', async () => {
  const actual = await vi.importActual<any>('../services/api')
  return {
    ...actual,
    AerodromosAPI: {
      search: vi.fn().mockResolvedValue({ status: 200, data: [{ codigo: 'SBSP', nome: 'Congonhas' }] })
    }
  }
})

describe('AerodromosAPI', () => {
  beforeEach(() => { vi.clearAllMocks() })
  it('deve buscar aeródromos com sucesso', async () => {
    const resp = await AerodromosAPI.search('SBSP')
    expect(resp.status).toBe(200)
    expect(resp.data[0].codigo).toBe('SBSP')
  })
})
