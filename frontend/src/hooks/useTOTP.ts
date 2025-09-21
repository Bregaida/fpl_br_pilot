import { useState } from 'react';
import { apiClient } from '../services/api';

export const useTOTP = () => {
  const [loading, setLoading] = useState(false);
  const [qrCode, setQrCode] = useState<string | null>(null);
  const [otpauthUrl, setOtpauthUrl] = useState<string | null>(null);
  const [sessionId, setSessionId] = useState<string | null>(null);
  const [verified, setVerified] = useState(false);

  const generateQR = async (email: string) => {
    setLoading(true);
    try {
      const response = await apiClient.prepareTOTP(email);
      setQrCode(response.qrPngBase64);
      setOtpauthUrl(response.otpauthUrl);
      setSessionId(response.totpSessionId);
      setVerified(false);
      return response;
    } catch (error) {
      console.error('Erro ao gerar QR Code:', error);
      throw error;
    } finally {
      setLoading(false);
    }
  };

  const verifyCode = async (codigo: string) => {
    if (!sessionId) {
      throw new Error('Sessão TOTP não encontrada');
    }

    setLoading(true);
    try {
      await apiClient.verifyTOTP(sessionId, codigo);
      setVerified(true);
      return true;
    } catch (error) {
      console.error('Erro ao verificar código TOTP:', error);
      throw error;
    } finally {
      setLoading(false);
    }
  };

  const reset = () => {
    setQrCode(null);
    setOtpauthUrl(null);
    setSessionId(null);
    setVerified(false);
  };

  return {
    loading,
    qrCode,
    otpauthUrl,
    sessionId,
    verified,
    generateQR,
    verifyCode,
    reset,
  };
};
