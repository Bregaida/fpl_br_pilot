import { Router } from 'express';
import { postComposeFpl, getHealth } from '../controllers/fplController';

const router = Router();

// Health check endpoint
router.get('/health', getHealth);

// FPL composition endpoint
router.post('/api/compose/fpl', postComposeFpl);

// Proxy endpoints
router.get('/api/lookup/aerodromo', async (req, res) => {
  try {
    const { icao, dof } = req.query;
    if (!icao || !dof) {
      return res.status(400).json({ 
        success: false, 
        error: { error: 'MISSING_PARAMS', message: 'ICAO and DOF are required' },
        timestamp: new Date().toISOString()
      });
    }
    
    // Forward the request to the backend
    const response = await fetch(`${process.env.BACKEND_BASE_URL}/api/v1/aerodromos/${icao}?dof=${dof}`);
    const data = await response.json();
    
    if (!response.ok) {
      return res.status(response.status).json({
        success: false,
        error: { error: 'AERODROME_LOOKUP_ERROR', message: data.message || 'Failed to fetch aerodrome data' },
        timestamp: new Date().toISOString()
      });
    }
    
    res.json({
      success: true,
      data,
      timestamp: new Date().toISOString()
    });
  } catch (error) {
    console.error('Error in aerodrome lookup:', error);
    res.status(500).json({
      success: false,
      error: { 
        error: 'INTERNAL_SERVER_ERROR', 
        message: error instanceof Error ? error.message : 'Unknown error occurred'
      },
      timestamp: new Date().toISOString()
    });
  }
});

// Briefing endpoint
router.get('/api/briefing', async (req, res) => {
  try {
    const { icao } = req.query;
    if (!icao) {
      return res.status(400).json({ 
        success: false, 
        error: { error: 'MISSING_PARAM', message: 'ICAO is required' },
        timestamp: new Date().toISOString()
      });
    }
    
    // Forward the request to the backend
    const response = await fetch(`${process.env.BACKEND_BASE_URL}/api/v1/meteorologia/${icao}/briefing`);
    const data = await response.json();
    
    if (!response.ok) {
      return res.status(response.status).json({
        success: false,
        error: { error: 'BRIEFING_LOOKUP_ERROR', message: data.message || 'Failed to fetch briefing data' },
        timestamp: new Date().toISOString()
      });
    }
    
    res.json({
      success: true,
      data,
      timestamp: new Date().toISOString()
    });
  } catch (error) {
    console.error('Error in briefing lookup:', error);
    res.status(500).json({
      success: false,
      error: { 
        error: 'INTERNAL_SERVER_ERROR', 
        message: error instanceof Error ? error.message : 'Unknown error occurred'
      },
      timestamp: new Date().toISOString()
    });
  }
});

// NOTAMs endpoint
router.get('/api/notams', async (req, res) => {
  try {
    const { icao } = req.query;
    if (!icao) {
      return res.status(400).json({ 
        success: false, 
        error: { error: 'MISSING_PARAM', message: 'ICAO is required' },
        timestamp: new Date().toISOString()
      });
    }
    
    // Forward the request to the backend
    const response = await fetch(`${process.env.BACKEND_BASE_URL}/api/v1/ais/${icao}/notams`);
    const data = await response.json();
    
    if (!response.ok) {
      return res.status(response.status).json({
        success: false,
        error: { error: 'NOTAMS_LOOKUP_ERROR', message: data.message || 'Failed to fetch NOTAMs' },
        timestamp: new Date().toISOString()
      });
    }
    
    res.json({
      success: true,
      data: Array.isArray(data) ? data : (data.notams || []),
      timestamp: new Date().toISOString()
    });
  } catch (error) {
    console.error('Error in NOTAMs lookup:', error);
    res.status(500).json({
      success: false,
      error: { 
        error: 'INTERNAL_SERVER_ERROR', 
        message: error instanceof Error ? error.message : 'Unknown error occurred'
      },
      timestamp: new Date().toISOString()
    });
  }
});

export default router;
