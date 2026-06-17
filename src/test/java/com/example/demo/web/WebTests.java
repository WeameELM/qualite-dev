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

    @Test
    void creerVoiture_retourne200() throws Exception {
        String voitureJson = "{\"marque\":\"f\",\"prix\":100}";

        mockMvc.perform(post("/voiture")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(voitureJson))
                .andDo(print())
                .andExpect(status().isOk());

        verify(statistiqueImpl, times(1)).ajouter(any(Voiture.class));
    }

    @Test
    void getStatistiques_retourneEchantillon() throws Exception {
        Echantillon echantillon = new Echantillon(2, 100); // (nombreDeVoitures, prixMoyen)
        when(statistiqueImpl.prixMoyen()).thenReturn(echantillon);

        mockMvc.perform(get("/statistique")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombreDeVoitures").value(2))  // ✅ vrai nom du champ
                .andExpect(jsonPath("$.prixMoyen").value(100));      // ✅ vrai nom du champ
    }

    @Test
    void getStatistiques_sansVoiture_retourneErreur() throws Exception {
        when(statistiqueImpl.prixMoyen()).thenThrow(new ArithmeticException());

        mockMvc.perform(get("/statistique")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest()); // ✅ @ResponseStatus(HttpStatus.BAD_REQUEST)
    }
}