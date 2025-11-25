package com.espaciosdeportivos.controller;

import com.espaciosdeportivos.dto.ComentarioDTO;
import com.espaciosdeportivos.model.Comentario;
import com.espaciosdeportivos.service.IComentarioService;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@RequestMapping("/api/comentario")
@Validated
public class ComentarioController {

    private final IComentarioService comentarioService;
    private static final Logger logger = LoggerFactory.getLogger(ComentarioController.class);

    @Autowired
    public ComentarioController(IComentarioService comentarioService) {
        this.comentarioService = comentarioService;
    }

    @GetMapping
    public ResponseEntity<List<ComentarioDTO>> obtenerTodosLosComentarios() {
        logger.info("[COMENTARIO] Inicio obtenerTodosLosComentarios");
        List<ComentarioDTO> comentarios = comentarioService.obtenerTodosLosComentarios();
        logger.info("[COMENTARIO] Fin obtenerTodosLosComentarios");
        return ResponseEntity.ok(comentarios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ComentarioDTO> obtenerComentarioPorId(@PathVariable Long id) {
        logger.info("[COMENTARIO] Inicio obtenerComentarioPorId: {}", id);
        ComentarioDTO comentario = comentarioService.obtenerComentarioPorId(id);
        logger.info("[COMENTARIO] Fin obtenerComentarioPorId");
        return ResponseEntity.ok(comentario);
    }

    @PostMapping
    @Transactional
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ComentarioDTO> crearComentario(@Valid @RequestBody ComentarioDTO comentarioDTO) {
        logger.info("[COMENTARIO] Inicio crearComentario");
        ComentarioDTO creado = comentarioService.crearComentario(comentarioDTO);
        logger.info("[COMENTARIO] Fin crearComentario");
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<ComentarioDTO> actualizarComentario(@PathVariable Long id, @Valid @RequestBody ComentarioDTO comentarioDTO) {
        logger.info("[COMENTARIO] Inicio actualizarComentario: {}", id);
        ComentarioDTO actualizado = comentarioService.actualizarComentario(id, comentarioDTO);
        logger.info("[COMENTARIO] Fin actualizarComentario");
        return ResponseEntity.ok(actualizado);
    }

    @PutMapping("/{id}/eliminar")
    @Transactional
    public ResponseEntity<ComentarioDTO> eliminarComentario(@PathVariable Long id) {
        logger.info("[COMENTARIO] Inicio eliminarComentario: {}", id);
        ComentarioDTO eliminado = comentarioService.eliminarComentario(id);
        logger.info("[COMENTARIO] Fin eliminarComentario");
        return ResponseEntity.ok(eliminado);
    }

    @GetMapping("/{id}/lock")
    public ResponseEntity<Comentario> obtenerComentarioConBloqueo(@PathVariable Long id) {
        logger.info("[COMENTARIO] Inicio obtenerComentarioConBloqueo: {}", id);
        Comentario comentario = comentarioService.obtenerComentarioConBloqueo(id);
        logger.info("[COMENTARIO] Fin obtenerComentarioConBloqueo");
        return ResponseEntity.ok(comentario);
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<String> eliminarComentarioFisicamente(@PathVariable Long id) {
        logger.info("[COMENTARIO] Inicio eliminarComentarioFisicamente: {}", id);
        comentarioService.eliminarComentarioFisicamente(id);
        logger.info("[COMENTARIO] Fin eliminarComentarioFisicamente");
        return ResponseEntity.ok("Comentario eliminado físicamente");
    }
    /*@GetMapping("/cancha/{canchaId}")
    public ResponseEntity<List<ComentarioDTO>> getComentariosPorCancha(@PathVariable Long canchaId) {
        List<ComentarioDTO> comentarios = comentarioService.getComentariosPorCancha(canchaId);
        return ResponseEntity.ok(comentarios);
    }*/

    // ---------- OBTENER POR ID ----------
    /*@GetMapping("/{id}")
    public ResponseEntity<ComentarioDTO> obtenerPorId(@PathVariable Long id) {
        logger.info("Obteniendo comentario por ID: {}", id);
        ComentarioDTO dto = comentarioService.obtenerComentarioPorId(id);
        return ResponseEntity.ok(dto);
    }*/

    // ---------- OBTENER COMENTARIOS POR CANCHA ----------
    @GetMapping("/cancha/{canchaId}")
    public ResponseEntity<List<ComentarioDTO>> comentariosPorCancha(@PathVariable Long canchaId) {
        logger.info("Obteniendo comentarios de la cancha ID: {}", canchaId);
        List<ComentarioDTO> lista = comentarioService.getComentariosPorCancha(canchaId);
        return ResponseEntity.ok(lista);
    }

    // ---------- OBTENER COMENTARIOS POR CLIENTE ----------
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<ComentarioDTO>> comentariosPorCliente(@PathVariable Long clienteId) {
        logger.info("Obteniendo comentarios del cliente ID: {}", clienteId);
        List<ComentarioDTO> lista = comentarioService.getComentariosPorCliente(clienteId);
        return ResponseEntity.ok(lista);
    }

    // ---------- OBTENER COMENTARIOS MÁS RECIENTES ----------
    @GetMapping("/recientes")
    public ResponseEntity<List<ComentarioDTO>> comentariosMasRecientes() {
        logger.info("Obteniendo comentarios más recientes");
        List<ComentarioDTO> lista = comentarioService.getComentariosMasRecientes(10);
        return ResponseEntity.ok(lista);
    }
    // ---------- OBTENER COMENTARIOS CON MAYOR CALIFICACIÓN ----------
    @GetMapping("/mayor-calificacion")
    public ResponseEntity<List<ComentarioDTO>> comentariosMayorCalificacion() {
        logger.info("Obteniendo comentarios con mayor calificación");
        List<ComentarioDTO> lista = comentarioService.getComentariosMayorCalificacion(10);
        return ResponseEntity.ok(lista);
    }

    // ---------- OBTENER COMENTARIOS CON MAYOR CALIFICACIÓN Y MÁS RECIENTES ----------
    @GetMapping("/mayor-calificacion-recientes")
    public ResponseEntity<List<ComentarioDTO>> comentariosMayorCalificacionRecientes() {
        logger.info("Obteniendo comentarios con mayor calificación y recientes");
        List<ComentarioDTO> lista = comentarioService.getComentariosMayorCalificacionMasRecientes(10);  // ✅ AGREGAR 10
        return ResponseEntity.ok(lista);
    }

    // ---------- OBTENER COMENTARIOS POR CALIFICACIÓN ESPECÍFICA ----------
    @GetMapping("/calificacion/{calificacion}")
    public ResponseEntity<List<ComentarioDTO>> comentariosPorCalificacion(@PathVariable Integer calificacion) {
        logger.info("Obteniendo comentarios con calificación: {}", calificacion);
        List<ComentarioDTO> lista = comentarioService.getComentariosPorCalificacion(calificacion);
        return ResponseEntity.ok(lista);
    }

    // ---------- OBTENER COMENTARIOS POR ÁREA DEPORTIVA ----------
    @GetMapping("/area/{areaId}")
    public ResponseEntity<List<ComentarioDTO>> comentariosPorArea(@PathVariable Long areaId) {
        logger.info("Obteniendo comentarios del área deportiva ID: {}", areaId);
        List<ComentarioDTO> lista = comentarioService.getComentariosPorAreaDeportiva(areaId);
        return ResponseEntity.ok(lista);
    }
}
