package br.com.fplbr.pilot.fpl.infraestrutura.adaptadores;

import br.com.fplbr.pilot.fpl.aplicacao.dto.BriefingMeteorologico;
import br.com.fplbr.pilot.fpl.aplicacao.portas.PortaMeteorologia;
import br.com.fplbr.pilot.fpl.infraestrutura.clientes.redemet.ClienteRedemetApi;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.Collections;
import java.util.List;

@ApplicationScoped
public class PortaMeteorologiaRedemet implements PortaMeteorologia {

    @RestClient
    ClienteRedemetApi api;

    @Override
    public BriefingMeteorologico obterBriefing(String icao) {
        String metar = api.obterMetar(icao);
        String taf = api.obterTaf(icao);
        String sigmetRaw;
        try {
            sigmetRaw = api.obterSigmet(icao);
        } catch (Exception e) {
            sigmetRaw = "";
        }
        List<String> sigmet = (sigmetRaw == null || sigmetRaw.isBlank())
                ? Collections.emptyList()
                : List.of(sigmetRaw);
        return new BriefingMeteorologico(metar, taf, sigmet);
    }
}

