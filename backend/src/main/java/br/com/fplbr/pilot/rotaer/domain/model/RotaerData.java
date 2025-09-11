package br.com.fplbr.pilot.rotaer.domain.model;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

/**
 * Modelo de domínio para dados do ROTAER
 * Representa a estrutura canônica JSON conforme especificação
 */
public class RotaerData {
    
    private AerodromoInfo aerodromo;
    private List<PistaInfo> pistas;
    private List<ComunicacaoInfo> comunicacoes;
    private List<RdonavInfo> rdonav;
    private ServicosInfo servicos;
    private List<InfotempInfo> infotemp;
    private AreaTrajetoriaDecolagem areaTrajetoriaDecolagem;
    private FonteInfo fonte;
    
    // Constructors
    public RotaerData() {}
    
    public RotaerData(AerodromoInfo aerodromo, List<PistaInfo> pistas, 
                     List<ComunicacaoInfo> comunicacoes, List<RdonavInfo> rdonav,
                     ServicosInfo servicos, List<InfotempInfo> infotemp,
                     AreaTrajetoriaDecolagem areaTrajetoriaDecolagem, FonteInfo fonte) {
        this.aerodromo = aerodromo;
        this.pistas = pistas;
        this.comunicacoes = comunicacoes;
        this.rdonav = rdonav;
        this.servicos = servicos;
        this.infotemp = infotemp;
        this.areaTrajetoriaDecolagem = areaTrajetoriaDecolagem;
        this.fonte = fonte;
    }
    
    // Getters and setters
    public AerodromoInfo getAerodromo() { return aerodromo; }
    public void setAerodromo(AerodromoInfo aerodromo) { this.aerodromo = aerodromo; }
    
    public List<PistaInfo> getPistas() { return pistas; }
    public void setPistas(List<PistaInfo> pistas) { this.pistas = pistas; }
    
    public List<ComunicacaoInfo> getComunicacoes() { return comunicacoes; }
    public void setComunicacoes(List<ComunicacaoInfo> comunicacoes) { this.comunicacoes = comunicacoes; }
    
    public List<RdonavInfo> getRdonav() { return rdonav; }
    public void setRdonav(List<RdonavInfo> rdonav) { this.rdonav = rdonav; }
    
    public ServicosInfo getServicos() { return servicos; }
    public void setServicos(ServicosInfo servicos) { this.servicos = servicos; }
    
    public List<InfotempInfo> getInfotemp() { return infotemp; }
    public void setInfotemp(List<InfotempInfo> infotemp) { this.infotemp = infotemp; }
    
    public AreaTrajetoriaDecolagem getAreaTrajetoriaDecolagem() { return areaTrajetoriaDecolagem; }
    public void setAreaTrajetoriaDecolagem(AreaTrajetoriaDecolagem areaTrajetoriaDecolagem) { 
        this.areaTrajetoriaDecolagem = areaTrajetoriaDecolagem; 
    }
    
    public FonteInfo getFonte() { return fonte; }
    public void setFonte(FonteInfo fonte) { this.fonte = fonte; }
    
    // Inner classes
    public static class Coordenadas {
        private String latDms;
        private String lonDms;
        private Double latDd;
        private Double lonDd;
        
        public Coordenadas() {}
        
        public Coordenadas(String latDms, String lonDms, Double latDd, Double lonDd) {
            this.latDms = latDms;
            this.lonDms = lonDms;
            this.latDd = latDd;
            this.lonDd = lonDd;
        }
        
        // Getters and setters
        public String getLatDms() { return latDms; }
        public void setLatDms(String latDms) { this.latDms = latDms; }
        
        public String getLonDms() { return lonDms; }
        public void setLonDms(String lonDms) { this.lonDms = lonDms; }
        
        public Double getLatDd() { return latDd; }
        public void setLatDd(Double latDd) { this.latDd = latDd; }
        
        public Double getLonDd() { return lonDd; }
        public void setLonDd(Double lonDd) { this.lonDd = lonDd; }
    }
    
    public static class DistanciaDirecao {
        private Integer km;
        private String direcao;
        
        public DistanciaDirecao() {}
        
        public DistanciaDirecao(Integer km, String direcao) {
            this.km = km;
            this.direcao = direcao;
        }
        
        // Getters and setters
        public Integer getKm() { return km; }
        public void setKm(Integer km) { this.km = km; }
        
        public String getDirecao() { return direcao; }
        public void setDirecao(String direcao) { this.direcao = direcao; }
    }
    
    public static class AerodromoInfo {
        private String nome;
        private String icao;
        private String municipio;
        private String uf;
        private Coordenadas coordsArp;
        private Integer elevacaoM;
        private Integer elevacaoFt;
        private String tipo;
        private String categoria;
        private String utilizacao;
        private String administrador;
        private DistanciaDirecao distanciaDirecaoCidade;
        private String fuso;
        private String operacao;
        private List<String> luzesAerodromo;
        private String observacoesGerais;
        private String fir;
        private String jurisdicao;
        private Map<String, Object> extras;
        
        // Getters and setters
        public String getNome() { return nome; }
        public void setNome(String nome) { this.nome = nome; }
        
        public String getIcao() { return icao; }
        public void setIcao(String icao) { this.icao = icao; }
        
        public String getMunicipio() { return municipio; }
        public void setMunicipio(String municipio) { this.municipio = municipio; }
        
        public String getUf() { return uf; }
        public void setUf(String uf) { this.uf = uf; }
        
        public Coordenadas getCoordsArp() { return coordsArp; }
        public void setCoordsArp(Coordenadas coordsArp) { this.coordsArp = coordsArp; }
        
        public Integer getElevacaoM() { return elevacaoM; }
        public void setElevacaoM(Integer elevacaoM) { this.elevacaoM = elevacaoM; }
        
        public Integer getElevacaoFt() { return elevacaoFt; }
        public void setElevacaoFt(Integer elevacaoFt) { this.elevacaoFt = elevacaoFt; }
        
        public String getTipo() { return tipo; }
        public void setTipo(String tipo) { this.tipo = tipo; }
        
        public String getCategoria() { return categoria; }
        public void setCategoria(String categoria) { this.categoria = categoria; }
        
        public String getUtilizacao() { return utilizacao; }
        public void setUtilizacao(String utilizacao) { this.utilizacao = utilizacao; }
        
        public String getAdministrador() { return administrador; }
        public void setAdministrador(String administrador) { this.administrador = administrador; }
        
        public DistanciaDirecao getDistanciaDirecaoCidade() { return distanciaDirecaoCidade; }
        public void setDistanciaDirecaoCidade(DistanciaDirecao distanciaDirecaoCidade) { 
            this.distanciaDirecaoCidade = distanciaDirecaoCidade; 
        }
        
        public String getFuso() { return fuso; }
        public void setFuso(String fuso) { this.fuso = fuso; }
        
        public String getOperacao() { return operacao; }
        public void setOperacao(String operacao) { this.operacao = operacao; }
        
        public List<String> getLuzesAerodromo() { return luzesAerodromo; }
        public void setLuzesAerodromo(List<String> luzesAerodromo) { this.luzesAerodromo = luzesAerodromo; }
        
        public String getObservacoesGerais() { return observacoesGerais; }
        public void setObservacoesGerais(String observacoesGerais) { this.observacoesGerais = observacoesGerais; }
        
        public String getFir() { return fir; }
        public void setFir(String fir) { this.fir = fir; }
        
        public String getJurisdicao() { return jurisdicao; }
        public void setJurisdicao(String jurisdicao) { this.jurisdicao = jurisdicao; }
        
        public Map<String, Object> getExtras() { return extras; }
        public void setExtras(Map<String, Object> extras) { this.extras = extras; }
    }
    
    public static class DimensoesPista {
        private Integer comprimentoM;
        private Integer larguraM;
        
        public DimensoesPista() {}
        
        public DimensoesPista(Integer comprimentoM, Integer larguraM) {
            this.comprimentoM = comprimentoM;
            this.larguraM = larguraM;
        }
        
        // Getters and setters
        public Integer getComprimentoM() { return comprimentoM; }
        public void setComprimentoM(Integer comprimentoM) { this.comprimentoM = comprimentoM; }
        
        public Integer getLarguraM() { return larguraM; }
        public void setLarguraM(Integer larguraM) { this.larguraM = larguraM; }
    }
    
    public static class MehtInfo {
        private String cabecera;
        private Double valorFt;
        
        public MehtInfo() {}
        
        public MehtInfo(String cabecera, Double valorFt) {
            this.cabecera = cabecera;
            this.valorFt = valorFt;
        }
        
        // Getters and setters
        public String getCabecera() { return cabecera; }
        public void setCabecera(String cabecera) { this.cabecera = cabecera; }
        
        public Double getValorFt() { return valorFt; }
        public void setValorFt(Double valorFt) { this.valorFt = valorFt; }
    }
    
    public static class PistaInfo {
        private List<String> designadores;
        private DimensoesPista dimensoes;
        private String piso;
        private String pcn;
        private List<String> luzes;
        private MehtInfo meht;
        private String observacoes;
        private Map<String, Object> extras;
        
        // Getters and setters
        public List<String> getDesignadores() { return designadores; }
        public void setDesignadores(List<String> designadores) { this.designadores = designadores; }
        
        public DimensoesPista getDimensoes() { return dimensoes; }
        public void setDimensoes(DimensoesPista dimensoes) { this.dimensoes = dimensoes; }
        
        public String getPiso() { return piso; }
        public void setPiso(String piso) { this.piso = piso; }
        
        public String getPcn() { return pcn; }
        public void setPcn(String pcn) { this.pcn = pcn; }
        
        public List<String> getLuzes() { return luzes; }
        public void setLuzes(List<String> luzes) { this.luzes = luzes; }
        
        public MehtInfo getMeht() { return meht; }
        public void setMeht(MehtInfo meht) { this.meht = meht; }
        
        public String getObservacoes() { return observacoes; }
        public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
        
        public Map<String, Object> getExtras() { return extras; }
        public void setExtras(Map<String, Object> extras) { this.extras = extras; }
    }
    
    public static class ComunicacaoInfo {
        private String orgaos;
        private String indicativo;
        private List<Double> frequenciasMhz;
        private List<Integer> observacaoComplRef;
        private String horarioOperacao;
        private Boolean emergencia;
        private String tipo;
        private Map<String, Object> extras;
        
        // Getters and setters
        public String getOrgaos() { return orgaos; }
        public void setOrgaos(String orgaos) { this.orgaos = orgaos; }
        
        public String getIndicativo() { return indicativo; }
        public void setIndicativo(String indicativo) { this.indicativo = indicativo; }
        
        public List<Double> getFrequenciasMhz() { return frequenciasMhz; }
        public void setFrequenciasMhz(List<Double> frequenciasMhz) { this.frequenciasMhz = frequenciasMhz; }
        
        public List<Integer> getObservacaoComplRef() { return observacaoComplRef; }
        public void setObservacaoComplRef(List<Integer> observacaoComplRef) { this.observacaoComplRef = observacaoComplRef; }
        
        public String getHorarioOperacao() { return horarioOperacao; }
        public void setHorarioOperacao(String horarioOperacao) { this.horarioOperacao = horarioOperacao; }
        
        public Boolean getEmergencia() { return emergencia; }
        public void setEmergencia(Boolean emergencia) { this.emergencia = emergencia; }
        
        public String getTipo() { return tipo; }
        public void setTipo(String tipo) { this.tipo = tipo; }
        
        public Map<String, Object> getExtras() { return extras; }
        public void setExtras(Map<String, Object> extras) { this.extras = extras; }
    }
    
    public static class RdonavInfo {
        private String tipo;
        private String ident;
        private Double freq;
        private Coordenadas coords;
        private String pistaAssociada;
        private String catIls;
        private Map<String, Object> extras;
        
        // Getters and setters
        public String getTipo() { return tipo; }
        public void setTipo(String tipo) { this.tipo = tipo; }
        
        public String getIdent() { return ident; }
        public void setIdent(String ident) { this.ident = ident; }
        
        public Double getFreq() { return freq; }
        public void setFreq(Double freq) { this.freq = freq; }
        
        public Coordenadas getCoords() { return coords; }
        public void setCoords(Coordenadas coords) { this.coords = coords; }
        
        public String getPistaAssociada() { return pistaAssociada; }
        public void setPistaAssociada(String pistaAssociada) { this.pistaAssociada = pistaAssociada; }
        
        public String getCatIls() { return catIls; }
        public void setCatIls(String catIls) { this.catIls = catIls; }
        
        public Map<String, Object> getExtras() { return extras; }
        public void setExtras(Map<String, Object> extras) { this.extras = extras; }
    }
    
    public static class RffsInfo {
        private Integer catCivil;
        private Integer catMil;
        
        public RffsInfo() {}
        
        public RffsInfo(Integer catCivil, Integer catMil) {
            this.catCivil = catCivil;
            this.catMil = catMil;
        }
        
        // Getters and setters
        public Integer getCatCivil() { return catCivil; }
        public void setCatCivil(Integer catCivil) { this.catCivil = catCivil; }
        
        public Integer getCatMil() { return catMil; }
        public void setCatMil(Integer catMil) { this.catMil = catMil; }
    }
    
    public static class MetInfo {
        private List<String> cma;
        private List<String> cmm;
        private List<String> telefones;
        
        public MetInfo() {}
        
        public MetInfo(List<String> cma, List<String> cmm, List<String> telefones) {
            this.cma = cma;
            this.cmm = cmm;
            this.telefones = telefones;
        }
        
        // Getters and setters
        public List<String> getCma() { return cma; }
        public void setCma(List<String> cma) { this.cma = cma; }
        
        public List<String> getCmm() { return cmm; }
        public void setCmm(List<String> cmm) { this.cmm = cmm; }
        
        public List<String> getTelefones() { return telefones; }
        public void setTelefones(List<String> telefones) { this.telefones = telefones; }
    }
    
    public static class AisInfo {
        private List<String> telefones;
        private List<String> mil;
        
        public AisInfo() {}
        
        public AisInfo(List<String> telefones, List<String> mil) {
            this.telefones = telefones;
            this.mil = mil;
        }
        
        // Getters and setters
        public List<String> getTelefones() { return telefones; }
        public void setTelefones(List<String> telefones) { this.telefones = telefones; }
        
        public List<String> getMil() { return mil; }
        public void setMil(List<String> mil) { this.mil = mil; }
    }
    
    public static class ComplInfo {
        private Integer ref;
        private String texto;
        
        public ComplInfo() {}
        
        public ComplInfo(Integer ref, String texto) {
            this.ref = ref;
            this.texto = texto;
        }
        
        // Getters and setters
        public Integer getRef() { return ref; }
        public void setRef(Integer ref) { this.ref = ref; }
        
        public String getTexto() { return texto; }
        public void setTexto(String texto) { this.texto = texto; }
    }
    
    public static class ServicosInfo {
        private List<String> combustivel;
        private List<String> manutencao;
        private RffsInfo rffs;
        private MetInfo met;
        private AisInfo ais;
        private String rmk;
        private List<ComplInfo> compl;
        private Map<String, Object> extras;
        
        // Getters and setters
        public List<String> getCombustivel() { return combustivel; }
        public void setCombustivel(List<String> combustivel) { this.combustivel = combustivel; }
        
        public List<String> getManutencao() { return manutencao; }
        public void setManutencao(List<String> manutencao) { this.manutencao = manutencao; }
        
        public RffsInfo getRffs() { return rffs; }
        public void setRffs(RffsInfo rffs) { this.rffs = rffs; }
        
        public MetInfo getMet() { return met; }
        public void setMet(MetInfo met) { this.met = met; }
        
        public AisInfo getAis() { return ais; }
        public void setAis(AisInfo ais) { this.ais = ais; }
        
        public String getRmk() { return rmk; }
        public void setRmk(String rmk) { this.rmk = rmk; }
        
        public List<ComplInfo> getCompl() { return compl; }
        public void setCompl(List<ComplInfo> compl) { this.compl = compl; }
        
        public Map<String, Object> getExtras() { return extras; }
        public void setExtras(Map<String, Object> extras) { this.extras = extras; }
    }
    
    public static class InfotempInfo {
        private String id;
        private OffsetDateTime inicio;
        private OffsetDateTime fim;
        private String texto;
        private String natureza;
        
        public InfotempInfo() {}
        
        public InfotempInfo(String id, OffsetDateTime inicio, OffsetDateTime fim, String texto, String natureza) {
            this.id = id;
            this.inicio = inicio;
            this.fim = fim;
            this.texto = texto;
            this.natureza = natureza;
        }
        
        // Getters and setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        
        public OffsetDateTime getInicio() { return inicio; }
        public void setInicio(OffsetDateTime inicio) { this.inicio = inicio; }
        
        public OffsetDateTime getFim() { return fim; }
        public void setFim(OffsetDateTime fim) { this.fim = fim; }
        
        public String getTexto() { return texto; }
        public void setTexto(String texto) { this.texto = texto; }
        
        public String getNatureza() { return natureza; }
        public void setNatureza(String natureza) { this.natureza = natureza; }
    }
    
    public static class AreaTrajetoriaDecolagem {
        private String origem;
        private Integer larguraOrigemM;
        private Integer larguraMaxM;
        private String taxaAumento;
        private Integer alcanceMaxM;
        private String observacao;
        
        public AreaTrajetoriaDecolagem() {}
        
        public AreaTrajetoriaDecolagem(String origem, Integer larguraOrigemM, Integer larguraMaxM, 
                                     String taxaAumento, Integer alcanceMaxM, String observacao) {
            this.origem = origem;
            this.larguraOrigemM = larguraOrigemM;
            this.larguraMaxM = larguraMaxM;
            this.taxaAumento = taxaAumento;
            this.alcanceMaxM = alcanceMaxM;
            this.observacao = observacao;
        }
        
        // Getters and setters
        public String getOrigem() { return origem; }
        public void setOrigem(String origem) { this.origem = origem; }
        
        public Integer getLarguraOrigemM() { return larguraOrigemM; }
        public void setLarguraOrigemM(Integer larguraOrigemM) { this.larguraOrigemM = larguraOrigemM; }
        
        public Integer getLarguraMaxM() { return larguraMaxM; }
        public void setLarguraMaxM(Integer larguraMaxM) { this.larguraMaxM = larguraMaxM; }
        
        public String getTaxaAumento() { return taxaAumento; }
        public void setTaxaAumento(String taxaAumento) { this.taxaAumento = taxaAumento; }
        
        public Integer getAlcanceMaxM() { return alcanceMaxM; }
        public void setAlcanceMaxM(Integer alcanceMaxM) { this.alcanceMaxM = alcanceMaxM; }
        
        public String getObservacao() { return observacao; }
        public void setObservacao(String observacao) { this.observacao = observacao; }
    }
    
    public static class FonteInfo {
        private String documento;
        private String icao;
        private String versao;
        private OffsetDateTime capturadoEm;
        
        public FonteInfo() {}
        
        public FonteInfo(String documento, String icao, String versao, OffsetDateTime capturadoEm) {
            this.documento = documento;
            this.icao = icao;
            this.versao = versao;
            this.capturadoEm = capturadoEm;
        }
        
        // Getters and setters
        public String getDocumento() { return documento; }
        public void setDocumento(String documento) { this.documento = documento; }
        
        public String getIcao() { return icao; }
        public void setIcao(String icao) { this.icao = icao; }
        
        public String getVersao() { return versao; }
        public void setVersao(String versao) { this.versao = versao; }
        
        public OffsetDateTime getCapturadoEm() { return capturadoEm; }
        public void setCapturadoEm(OffsetDateTime capturadoEm) { this.capturadoEm = capturadoEm; }
    }
}
