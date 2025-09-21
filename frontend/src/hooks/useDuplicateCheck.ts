import { useState } from 'react';
import { apiClient } from '../services/api';

export const useDuplicateCheck = () => {
  const [checking, setChecking] = useState(false);
  const [emailError, setEmailError] = useState<string | null>(null);
  const [cpfError, setCpfError] = useState<string | null>(null);

  const checkEmail = async (email: string): Promise<boolean> => {
    if (!email || email.trim().length === 0) {
      setEmailError(null);
      return false;
    }

    setChecking(true);
    setEmailError(null);

    try {
      await apiClient.checkDuplicate({ email: email.toLowerCase().trim() });
      return false; // Não é duplicado
    } catch (error: any) {
      const errorMessage = error?.response?.data?.error || error?.message;
      if (errorMessage?.includes('E-mail já cadastrado')) {
        setEmailError(`Já existe E-mail cadastrado na base, utilize o autenticador ou recupere sua senha.`);
        return true;
      }
      return false;
    } finally {
      setChecking(false);
    }
  };

  const checkCpf = async (cpf: string): Promise<boolean> => {
    if (!cpf || cpf.replace(/\D/g, '').length !== 11) {
      setCpfError(null);
      return false;
    }

    setChecking(true);
    setCpfError(null);

    try {
      await apiClient.checkDuplicate({ cpf: cpf.replace(/\D/g, '') });
      return false; // Não é duplicado
    } catch (error: any) {
      const errorMessage = error?.response?.data?.error || error?.message;
      if (errorMessage?.includes('CPF já cadastrado')) {
        setCpfError(`Já existe CPF cadastrado na base, utilize o autenticador ou recupere sua senha.`);
        return true;
      }
      return false;
    } finally {
      setChecking(false);
    }
  };

  const clearEmailError = () => {
    setEmailError(null);
  };

  const clearCpfError = () => {
    setCpfError(null);
  };

  const clearAllErrors = () => {
    setEmailError(null);
    setCpfError(null);
  };

  return {
    checking,
    emailError,
    cpfError,
    checkEmail,
    checkCpf,
    clearEmailError,
    clearCpfError,
    clearAllErrors
  };
};
