package com.example.demo.web;

import com.example.demo.data.Voiture;
import com.example.demo.service.Echantillon;
import com.example.demo.service.StatistiqueImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class WebTests {

    @MockBean
    StatistiqueImpl statistiqueImpl;

    @Autowired
    MockMvc mockMvc;

    // ─── Test 1 : POST /voiture ───────────────────────────────────────────────

    @Test
    void creerVoiture_retourne200() throws Exception {
        // Prépare le corps JSON (équivalent du curl fourni)
        String voitureJson = "{\"marque\":\"f\",\"prix\":100}";

        mockMvc.perform(post("/voiture")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(voitureJson))
                .andDo(print())
                .andExpect(status().isOk());   // 200 par défaut pour void

        // Vérifie que la méthode ajouter() du service a bien été appelée
        verify(statistiqueImpl, times(1)).ajouter(any(Voiture.class));
    }

    // ─── Test 2 : GET /statistique – cas nominal ──────────────────────────────

    @Test
    void getStatistiques_retourneEchantillon() throws Exception {
        // Stubbing : le mock retourne un Echantillon connu
        Echantillon echantillon = new Echantillon(100, 2);  // à adapter selon constructeur
        when(statistiqueImpl.prixMoyen()).thenReturn(echantillon);

        mockMvc.perform(get("/statistique")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.moyenne").value(100.0))   // à adapter au(x) champ(s) d'Echantillon
                .andExpect(jsonPath("$.taille").value(2));
    }

    // ─── Test 3 : GET /statistique – aucune voiture → 404/exception ──────────

    @Test
    void getStatistiques_sansVoiture_retourneErreur() throws Exception {
        // Stubbing : le service lève ArithmeticException (division par zéro)
        when(statistiqueImpl.prixMoyen()).thenThrow(new ArithmeticException());

        mockMvc.perform(get("/statistique")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                // PasDeVoitureException produit un 500 par défaut
                // (ou 404/400 si elle est annotée @ResponseStatus)
                .andExpect(status().isInternalServerError());
    }
}