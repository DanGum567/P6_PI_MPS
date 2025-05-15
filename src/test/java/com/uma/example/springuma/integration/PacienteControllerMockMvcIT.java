package com.uma.example.springuma.integration;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.uma.example.springuma.model.Medico;
import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.uma.example.springuma.integration.base.AbstractIntegration;
import com.uma.example.springuma.model.Paciente;

import java.time.Duration;

public class PacienteControllerMockMvcIT extends AbstractIntegration {
    @Autowired
    public MockMvc mockMvc;
    @Autowired
    public ObjectMapper objectMapper;

    // Entidades
    public Paciente paciente;
    public Medico medico;

    @PostConstruct
    public void init() throws Exception{
        paciente = new Paciente();
        paciente.setNombre("Paciente");
        paciente.setDni("123");
        paciente.setEdad(96);
        paciente.setId(1);

        medico = new Medico();
        medico.setNombre("Medico");
        medico.setDni("122");
        medico.setId(1);
        medico.setEspecialidad("Cardiologia");

        paciente.setMedico(medico);

        // Almacenamos al médico en la base de datos, no realizamos comprobaciones
        // dado que esto se realiza en otra clase

        this.mockMvc.perform(post("/medico")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(medico)))
                        .andExpect(status().is2xxSuccessful());

    }

    @Test
    @DisplayName("Checks that initially there is no patient in the system")
    public void getPacienteGET_initially_doesNotExists() throws Exception{

        // Comprobamos la ausencia con el id del paciente
        this.mockMvc.perform(get("/paciente/1"))
                .andExpect(content().contentType("application/json"))
                .andExpect(status().isInternalServerError());
    }


    @Test
    @DisplayName("Checks that initially the lists of patients is empty")
    public void getPacientesGET_initially_isEmpty() throws Exception{
        // La condición de la ausencia de pacientes se comprueba con
        // el id del médico
        this.mockMvc.perform(get("/paciente/medico/1"))
                .andExpect(content().contentType("application/json"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$", hasSize(0)));
    }


    @Test
    @DisplayName("Checks if patient is saved correctly")
    public void savePacientePOST_obtainedWithGET() throws Exception{
        // Guardamos al paciente en la base de datos usando POST
        this.mockMvc.perform(post("/paciente")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(paciente)))
                .andExpect(status().is2xxSuccessful());

        // Comprobamos si el paciente se ha almacenado correctamente usando GET

        // Comprobación con el id del paciente
        this.mockMvc.perform(get("/paciente/1"))
                .andExpect(content().contentType("application/json"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.dni").value("123"))
                .andExpect(jsonPath("$.nombre").value("Paciente"))
                .andExpect(jsonPath("$.edad").value(96))
                .andExpect(jsonPath("$.id").value(1));

        /* Comprobamos que la lista de los pacientes del médico asociado al paciente
        *  se ha actualizado correctamente */

        this.mockMvc.perform(get("/paciente/medico/1"))
                .andExpect(content().contentType("application/json"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].dni").value("123"))
                .andExpect(jsonPath("$[0].nombre").value("Paciente"))
                .andExpect(jsonPath("$[0].edad").value(96))
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    @DisplayName("Checks if patient is updated correctly")
    public void updatePacientePUT_obtainedWithGET() throws Exception{
        // Guardamos al paciente en la base de datos usando POST
        this.mockMvc.perform(post("/paciente")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(paciente)))
                .andExpect(status().is2xxSuccessful());

        // Comprobamos si el paciente se ha almacenado correctamente usando GET

        // Comprobación con el id del paciente
        this.mockMvc.perform(get("/paciente/1"))
                .andExpect(content().contentType("application/json"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.dni").value("123"))
                .andExpect(jsonPath("$.nombre").value("Paciente"))
                .andExpect(jsonPath("$.edad").value(96))
                .andExpect(jsonPath("$.id").value(1));

        /* Comprobamos que la lista de los pacientes del médico asociado al paciente
         *  se ha actualizado correctamente */

        this.mockMvc.perform(get("/paciente/medico/1"))
                .andExpect(content().contentType("application/json"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].dni").value("123"))
                .andExpect(jsonPath("$[0].nombre").value("Paciente"))
                .andExpect(jsonPath("$[0].edad").value(96))
                .andExpect(jsonPath("$[0].id").value(1));

        // Comprobamos si el paciente se modifica correctamente

        this.paciente.setNombre("Paciente 2");

        // Comprobamos accediendo al paciente directamente

        this.mockMvc.perform(put("/paciente")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(paciente)))
                .andExpect(status().is2xxSuccessful());

        this.mockMvc.perform(get("/paciente/1"))
                .andExpect(content().contentType("application/json"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.dni").value("123"))
                .andExpect(jsonPath("$.nombre").value("Paciente 2"))
                .andExpect(jsonPath("$.edad").value(96))
                .andExpect(jsonPath("$.id").value(1));

        this.mockMvc.perform(get("/paciente/medico/1"))
                .andExpect(content().contentType("application/json"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].dni").value("123"))
                .andExpect(jsonPath("$[0].nombre").value("Paciente 2"))
                .andExpect(jsonPath("$[0].edad").value(96))
                .andExpect(jsonPath("$[0].id").value(1));
    }
     // Comprobamos accediendo a la lista de los pacientes
     // del médico asociado al paciente
     //
     // El paciente modificado es el único quien está en la lista

    @Test
    @DisplayName("Checks if patient is updated correctly")
    public void deletePacienteDELETE_obtainedWithGET() throws Exception{
        // Guardamos al paciente en la base de datos usando POST
        this.mockMvc.perform(post("/paciente")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(paciente)))
                .andExpect(status().is2xxSuccessful());

        // Comprobamos si el paciente se ha almacenado correctamente usando GET

        // Comprobación con el id del paciente
        this.mockMvc.perform(get("/paciente/1"))
                .andExpect(content().contentType("application/json"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.dni").value("123"))
                .andExpect(jsonPath("$.nombre").value("Paciente"))
                .andExpect(jsonPath("$.edad").value(96))
                .andExpect(jsonPath("$.id").value(1));

        /* Comprobamos que la lista de los pacientes del médico asociado al paciente
         *  se ha actualizado correctamente */

        this.mockMvc.perform(get("/paciente/medico/1"))
                .andExpect(content().contentType("application/json"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].dni").value("123"))
                .andExpect(jsonPath("$[0].nombre").value("Paciente"))
                .andExpect(jsonPath("$[0].edad").value(96))
                .andExpect(jsonPath("$[0].id").value(1));

        // Comprobamos si el paciente se elimina correctamente

        this.mockMvc.perform(delete("/paciente/1")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(paciente)))
                .andExpect(status().is2xxSuccessful());

        // Comprobamos accediendo al paciente directamente
        this.mockMvc.perform(get("/paciente/1"))
                .andExpect(content().contentType("application/json"))
                .andExpect(status().is5xxServerError());

        this.mockMvc.perform(get("/paciente/medico/1"))
                .andExpect(content().contentType("application/json"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}
