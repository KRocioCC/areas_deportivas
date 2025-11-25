package com.espaciosdeportivos.controller;

import com.espaciosdeportivos.dto.CanchaDTO;
import com.espaciosdeportivos.dto.DisciplinaDTO;
import com.espaciosdeportivos.dto.EquipamientoDTO;
import com.espaciosdeportivos.dto.ReservaDTO;
import com.espaciosdeportivos.model.Cancha;
import com.espaciosdeportivos.service.ICanchaService;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.springframework.http.MediaType;

@RestController
@RequestMapping("/api/cancha")
@Validated
public class CanchaController {

    private final ICanchaService canchaService;
    private static final Logger logger = LoggerFactory.getLogger(CanchaController.class);

    @Autowired
    public CanchaController(ICanchaService canchaService) {
        this.canchaService = canchaService;
    }

    @GetMapping("/activos")
    public ResponseEntity<List<CanchaDTO>> obtenerTodasLasCanchas() {
        logger.info("[AREA] Inicio obtenerTodas");
        List<CanchaDTO> lista = canchaService.obtenerTodasLasCanchas();
        logger.info("[AREA] Fin obtenerTodas");
        return ResponseEntity.ok(lista);
    }

    @GetMapping
    public ResponseEntity<List<CanchaDTO>> ListarTodos() {
        logger.info("[CANCHA] Inicio obtenerTodasLasCanchas");
        List<CanchaDTO> lista = canchaService.ListarTodos();
        logger.info("[CANCHA] Fin obtenerTodasLasCanchas ({} registros)", lista.size());
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/porid/{id}")
    public ResponseEntity<CanchaDTO> obtenerCanchaPorId(@PathVariable Long id) {
        logger.info("[CANCHA] Inicio obtenerCanchaPorId: {}", id);
        CanchaDTO dto = canchaService.obtenerCanchaPorId(id);
        logger.info("[CANCHA] Fin obtenerCanchaPorId");
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    @Transactional
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<CanchaDTO> crearCancha(@Valid @RequestBody CanchaDTO canchaDTO) {
        logger.info("[CANCHA] Inicio crearCancha");
        CanchaDTO creado = canchaService.crearCancha(canchaDTO);
        logger.info("[CANCHA] Fin crearCancha: id={}", creado.getIdCancha());
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<CanchaDTO> actualizarCancha(@PathVariable Long id, @Valid @RequestBody CanchaDTO canchaDTO) {
        logger.info("[CANCHA] Inicio actualizarCancha: {}", id);
        CanchaDTO actualizado = canchaService.actualizarCancha(id, canchaDTO);
        logger.info("[CANCHA] Fin actualizarCancha: {}", id);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    @Transactional
    public void eliminar(@PathVariable Long id) {
        canchaService.eliminarCanchaFisicamente(id);
    }

    @PatchMapping("/{id}/estado")
    @Transactional
    public ResponseEntity<CanchaDTO> cambiarEstadoCancha(@PathVariable Long id, @RequestParam Boolean nuevoEstado) {
        logger.info("[CANCHA] Inicio cambiarEstadoCancha: {}", id);
        CanchaDTO actualizada = canchaService.eliminarCancha(id, nuevoEstado);
        logger.info("[CANCHA] Fin cambiarEstadoCancha: {}", id);
        return ResponseEntity.ok(actualizada);
    }

    @GetMapping("/buscar/{nombre}")
    public ResponseEntity<List<CanchaDTO>> buscarPorNombre(@PathVariable String nombre) {
        List<CanchaDTO> resultados = canchaService.buscarPorNombre(nombre);
        return ResponseEntity.ok(resultados);
    }

    @GetMapping("/{id}/lock")
    public ResponseEntity<Cancha> obtenerCanchaConBloqueo(@PathVariable Long id) {
       Cancha cancha =canchaService.obtenerCanchaConBloqueo(id);
        return ResponseEntity.ok(cancha);
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<CanchaDTO>> buscarFiltrosCanchas(
            @RequestParam(required = false) LocalTime horaInicio,
            @RequestParam(required = false) LocalTime horaFin,
            @RequestParam(required = false) Double costo,
            @RequestParam(required = false) Integer capacidad,
            @RequestParam(required = false) String tamano,
            @RequestParam(required = false) String iluminacion,
            @RequestParam(required = false) String cubierta
    ) {
        System.out.println("horaInicio: " + horaInicio);
        System.out.println("horaFin: " + horaFin);
        System.out.println("costo: " + costo);
        List<CanchaDTO> resultados = canchaService.BuscarConFiltros(horaInicio, horaFin, costo, capacidad, tamano, iluminacion, cubierta);
        return ResponseEntity.ok(resultados);
    }

    @GetMapping("/{id}/equipamientos")
    public ResponseEntity<List<EquipamientoDTO>> obtenerEquipamientosPorCancha(@PathVariable Long id) {
        List<EquipamientoDTO> equipamientos = canchaService.obtenerEquipamientoPorCancha(id);
        return ResponseEntity.ok(equipamientos);
    }

    @GetMapping("/{id}/disciplinas")
    public ResponseEntity<List<DisciplinaDTO>> obtenerDisciplinasPorCancha(@PathVariable Long id) {
        List<DisciplinaDTO> equipamientos = canchaService.obtenerDiciplinasPorCancha(id);
        return ResponseEntity.ok(equipamientos);
    }

    /*@GetMapping("/{id}/reservas")
    public ResponseEntity<List<ReservaDTO>> obtenerReservasPorCancha(@PathVariable Long id) {
        List<ReservaDTO> reservas = canchaService.obtenerReservaPorCancha(id);
        return ResponseEntity.ok(reservas);
    }*/

    //gestion imagenes

    @PostMapping(value = "/{id}/imagenes", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Transactional
    public ResponseEntity<CanchaDTO> agregarImagenes(
            @PathVariable Long id,
            @RequestParam List<MultipartFile> archivosImagenes) {
        logger.info("[CANCHA] POST /api/cancha/{}/imagenes - {} archivos", id, archivosImagenes.size());
        CanchaDTO response = canchaService.agregarImagenes(id, archivosImagenes);
        return ResponseEntity.ok(response);
    }
    //aqui seran nuevas ediciones
    @DeleteMapping("/{id}/imagenes/{idImagenRelacion}")
    @Transactional
    public ResponseEntity<CanchaDTO> eliminarImagen(
            @PathVariable Long id,
            @PathVariable Long idImagenRelacion) {
        logger.info("[CANCHA] DELETE /api/cancha/{}/imagenes/{}", id, idImagenRelacion);
        CanchaDTO response = canchaService.eliminarImagen(id, idImagenRelacion);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/imagenes/reordenar")
    @Transactional
    public ResponseEntity<CanchaDTO> reordenarImagenes(
            @PathVariable Long id,
            @RequestBody List<Long> idsImagenesOrden) {
        logger.info("[CANCHA] PUT /api/cancha/{}/imagenes/reordenar - {} imágenes", id, idsImagenesOrden.size());
        CanchaDTO response = canchaService.reordenarImagenes(id, idsImagenesOrden);
        return ResponseEntity.ok(response);
    }


    /*// Baja lógica (estadobool = false)
    @PutMapping("/{id}/eliminar")
    @Transactional
    public ResponseEntity<CanchaDTO> eliminarCancha(@PathVariable Long id) {
        logger.info("[CANCHA] Inicio eliminarCancha (baja lógica): {}", id);
        CanchaDTO eliminado = canchaService.eliminarCancha(id);
        logger.info("[CANCHA] Fin eliminarCancha (baja lógica): {}", id);
        return ResponseEntity.ok(eliminado);
    } */
 

    //@PreAuthorize("hasRole('ROL_ADMIN')")
    //admin k obtener canchas por área deportiva

    @GetMapping("/area/{idArea}")
    public ResponseEntity<List<CanchaDTO>> obtenerCanchasPorArea(@PathVariable Long idArea) {
        logger.info("[CANCHA] Inicio obtenerCanchasPorArea: {}", idArea);
        List<CanchaDTO> canchas = canchaService.obtenerCanchasPorArea(idArea);
        logger.info("[CANCHA] Fin obtenerCanchasPorArea: {} canchas encontradas", canchas.size());
        return ResponseEntity.ok(canchas);
    }
    //nuevas enspoints
    @GetMapping("/mejor-calificadas")
    public ResponseEntity<List<CanchaDTO>> obtenerCanchasMejorCalificadas() {
        logger.info("[CANCHA] Inicio obtenerCanchasMejorCalificadas");
        List<CanchaDTO> lista = canchaService.obtenerCanchasMejorCalificadas();
        logger.info("[CANCHA] Fin obtenerCanchasMejorCalificadas");
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/mas-reservadas")
    public ResponseEntity<List<CanchaDTO>> obtenerCanchasMasReservadas() {
        logger.info("[CANCHA] Inicio obtenerCanchasMasReservadas");
        List<CanchaDTO> lista = canchaService.obtenerCanchasMasReservadas();
        logger.info("[CANCHA] Fin obtenerCanchasMasReservadas");
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/por-disciplina/{idDisciplina}")
    public ResponseEntity<List<CanchaDTO>> obtenerCanchasPorDisciplina(@PathVariable Long idDisciplina) {
        logger.info("[CANCHA] Inicio obtenerCanchasPorDisciplina idDisciplina={}", idDisciplina);
        List<CanchaDTO> lista = canchaService.obtenerCanchasPorDisciplina(idDisciplina);
        logger.info("[CANCHA] Fin obtenerCanchasPorDisciplina idDisciplina={}", idDisciplina);
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/por-zona/{idZona}")
    public ResponseEntity<List<CanchaDTO>> obtenerCanchasPorZona(@PathVariable Long idZona) {
        logger.info("[CANCHA] Inicio obtenerCanchasPorZona idZona={}", idZona);
        List<CanchaDTO> lista = canchaService.obtenerCanchasPorZona(idZona);
        logger.info("[CANCHA] Fin obtenerCanchasPorZona idZona={}", idZona);
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/abiertas-ahora")
    public ResponseEntity<List<CanchaDTO>> obtenerCanchasAbiertasAhora() {
        logger.info("[CANCHA] Inicio obtenerCanchasAbiertasAhora");
        List<CanchaDTO> lista = canchaService.obtenerCanchasAbiertas();
        logger.info("[CANCHA] Fin obtenerCanchasAbiertasAhora");
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/disponibles")
    public ResponseEntity<List<CanchaDTO>> obtenerCanchasDisponibles(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime horaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime horaFin) {
        logger.info("[CANCHA] Inicio obtenerCanchasDisponibles fecha={} horaInicio={} horaFin={}", fecha, horaInicio, horaFin);
        List<CanchaDTO> lista = canchaService.obtenerCanchasDisponibles(fecha, horaInicio, horaFin);
        logger.info("[CANCHA] Fin obtenerCanchasDisponibles fecha={} horaInicio={} horaFin={}", fecha, horaInicio, horaFin);
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/reservadas-cliente/{idCliente}")
    public ResponseEntity<List<CanchaDTO>> obtenerCanchasReservadasPorCliente(@PathVariable Long idCliente) {
        logger.info("[CANCHA] Inicio obtenerCanchasReservadasPorCliente idCliente={}", idCliente);
        List<CanchaDTO> lista = canchaService.obtenerCanchasReservadasPorCliente(idCliente);
        logger.info("[CANCHA] Fin obtenerCanchasReservadasPorCliente idCliente={}", idCliente);
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/buscar-por-disciplina")
    public ResponseEntity<List<CanchaDTO>> buscarCanchasPorNombreDisciplina(@RequestParam String nombreDisciplina) {
        logger.info("[CANCHA] Inicio buscarCanchasPorNombreDisciplina nombreDisciplina={}", nombreDisciplina);
        List<CanchaDTO> lista = canchaService.buscarCanchasPorNombreDisciplina(nombreDisciplina);
        logger.info("[CANCHA] Fin buscarCanchasPorNombreDisciplina nombreDisciplina={}", nombreDisciplina);
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/por-capacidad/{capacidad}")
    public ResponseEntity<List<CanchaDTO>> obtenerCanchasPorCapacidad(@PathVariable Integer capacidad) {
        logger.info("[CANCHA] Inicio obtenerCanchasPorCapacidad capacidad={}", capacidad);
        List<CanchaDTO> lista = canchaService.obtenerCanchasPorCapacidad(capacidad);
        logger.info("[CANCHA] Fin obtenerCanchasPorCapacidad capacidad={}", capacidad);
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/por-superficie")
    public ResponseEntity<List<CanchaDTO>> obtenerCanchasPorTipoSuperficie(@RequestParam String tipoSuperficie) {
        logger.info("[CANCHA] Inicio obtenerCanchasPorTipoSuperficie tipoSuperficie={}", tipoSuperficie);
        List<CanchaDTO> lista = canchaService.obtenerCanchasPorTipoSuperficie(tipoSuperficie);
        logger.info("[CANCHA] Fin obtenerCanchasPorTipoSuperficie tipoSuperficie={}", tipoSuperficie);
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/por-iluminacion")
    public ResponseEntity<List<CanchaDTO>> obtenerCanchasPorIluminacion(@RequestParam String iluminacion) {
        logger.info("[CANCHA] Inicio obtenerCanchasPorIluminacion iluminacion={}", iluminacion);
        List<CanchaDTO> lista = canchaService.obtenerCanchasPorIluminacion(iluminacion);
        logger.info("[CANCHA] Fin obtenerCanchasPorIluminacion iluminacion={}", iluminacion);
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/por-cubierta")
    public ResponseEntity<List<CanchaDTO>> obtenerCanchasPorCubierta(@RequestParam String cubierta) {
        logger.info("[CANCHA] Inicio obtenerCanchasPorCubierta cubierta={}", cubierta);
        List<CanchaDTO> lista = canchaService.obtenerCanchasPorCubierta(cubierta);
        logger.info("[CANCHA] Fin obtenerCanchasPorCubierta cubierta={}", cubierta);
        return ResponseEntity.ok(lista);
    }

}
