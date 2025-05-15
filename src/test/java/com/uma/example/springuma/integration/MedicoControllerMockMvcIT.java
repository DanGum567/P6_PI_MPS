package com.uma.example.springuma.integration;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.uma.example.springuma.integration.base.AbstractIntegration;
import com.uma.example.springuma.model.Medico;

public class MedicoControllerMockMvcIT extends AbstractIntegration{
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("Checks that initially the list of doctors is empty")
    public void getMedicoGET_initially_isEmpty() throws Exception{
        this.mockMvc.perform(get("/medico/123"))
                .andExpect(content().contentType("application/json"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("Checks that initially the list of doctors is empty")
    public void saveMedicoPOST_obtainedWithGET() throws Exception{
        // Creamos al médico
        Medico medico = new Medico();
        medico.setNombre("Antonio");
        medico.setDni("123");
        medico.setId(1);
        medico.setEspecialidad("Cardiologia");

        // Guardamos al médico en la base de datos
       this.mockMvc.perform(post("/medico")
               .contentType("application/json")
               .content(objectMapper.writeValueAsString(medico)))
               .andExpect(status().is2xxSuccessful());

       // Comprobamos si el médico se ha guardado correctamente
       this.mockMvc.perform(get("/medico/dni/123"))
               .andExpect(content().contentType("application/json"))
               .andExpect(status().is2xxSuccessful())
               .andExpect(jsonPath("$.dni").value("123"));
    }

    @Test
    @DisplayName("Checks if doctor is updated successfully")
    public void updateMedicoPOST_obtainedWithGET() throws Exception{
        // Creamos al médico
        Medico medico = new Medico();
        medico.setNombre("Valerio");
        medico.setDni("1234568");
        medico.setId(1);
        medico.setEspecialidad("Traumatologia");

        // Guardamos al médico en la base de datos usando POST
        this.mockMvc.perform(post("/medico")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(medico)))
                .andExpect(status().is2xxSuccessful());

        // Comprobamos si el médico se ha guardado correctamente usando GET
        this.mockMvc.perform(get("/medico/dni/1234568"))
                .andExpect(content().contentType("application/json"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.dni").value("1234568"))
                .andExpect(jsonPath("$.nombre").value("Valerio"))
                .andExpect(jsonPath("$.especialidad").value("Traumatologia"))
                .andExpect(jsonPath("$.id").value(1));

        medico.setEspecialidad("Cirugía plástica");

        // Actualizamos al médico en la base de datos usando PUT
        this.mockMvc.perform(put("/medico")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(medico)))
                .andExpect(status().is2xxSuccessful());

        // Comprobamos que el medico ha sido eliminado usando GET
        this.mockMvc.perform(get("/medico/dni/1234568"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.especialidad").value("Cirugía plástica"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.dni").value("1234568"));

    }

    @Test
    @DisplayName("Checks if doctor is updated successfully")
    public void deleteMedicoDELETE_obtainedWithGET() throws Exception{
        // Creamos al médico
        Medico medico = new Medico();
        medico.setNombre("Valerio");
        medico.setDni("1234568");
        medico.setId(1);
        medico.setEspecialidad("Traumatologia");

        // Guardamos al médico en la base de datos usando POST
        this.mockMvc.perform(post("/medico")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(medico)))
                .andExpect(status().is2xxSuccessful());

        // Comprobamos si el médico se ha guardado correctamente usando GET
        this.mockMvc.perform(get("/medico/dni/1234568"))
                .andExpect(content().contentType("application/json"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.dni").value("1234568"))
                .andExpect(jsonPath("$.nombre").value("Valerio"))
                .andExpect(jsonPath("$.especialidad").value("Traumatologia"))
                .andExpect(jsonPath("$.id").value(1));

        medico.setEspecialidad("Cirugía plástica");

        // Eliminamos al médico de la base de datos usando DELETE
        this.mockMvc.perform(delete("/medico/1"))
                .andExpect(status().is2xxSuccessful());

        // Comprobamos que el medico ha sido eliminado usando GET
        this.mockMvc.perform(get("/medico/1"))
                .andExpect(content().contentType("application/json"))
                .andExpect(status().is5xxServerError());
    }


 }

