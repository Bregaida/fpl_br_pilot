package br.com.fplbr.pilot.fpl.dominio.validacao;
import java.util.List;
public interface Especificacao<T> { List<ErroRegra> validar(T alvo); }

