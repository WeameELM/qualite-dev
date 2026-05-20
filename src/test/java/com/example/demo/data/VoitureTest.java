package com.example.demo.data;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class VoitureTest {

    @Test
    void creerVoiture(){
        Voiture wewe = new Voiture("Citroen", 1500) ; 
        Assert.isTrue(wewe.getMarque().equals("Citroen"), "Doit être Citroen") ; 
        Assert.isTrue(wewe.getPrix() == 1500, "Doit être 1500") ; 
        Assert.isTrue(wewe.getId() == 0, "Doit être 0") ; 
    }

}
