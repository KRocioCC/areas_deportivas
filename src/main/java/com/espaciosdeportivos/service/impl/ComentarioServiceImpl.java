package com.espaciosdeportivos.service.impl;

import com.espaciosdeportivos.dto.ComentarioDTO;
import com.espaciosdeportivos.dto.PersonaPublicaDTO;
import com.espaciosdeportivos.model.Persona;
import com.espaciosdeportivos.model.Cancha;
import com.espaciosdeportivos.model.Comentario;
import com.espaciosdeportivos.repository.AdministradorRepository;
import com.espaciosdeportivos.repository.ClienteRepository;
import com.espaciosdeportivos.repository.InvitadoRepository;
import com.espaciosdeportivos.repository.UsuarioControlRepository;
import com.espaciosdeportivos.repository.CanchaRepository;
import com.espaciosdeportivos.repository.ComentarioRepository;
import com.espaciosdeportivos.service.IComentarioService;
import com.espaciosdeportivos.validation.ComentarioValidator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.validation.Valid;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service
@RequiredArgsConstructor
@Transactional
public class ComentarioServiceImpl implements IComentarioService {

    private final ComentarioRepository comentarioRepository;
    private final CanchaRepository canchaRepository;
    private final ComentarioValidator comentarioValidator;

    private final AdministradorRepository administradorRepository;
    private final ClienteRepository clienteRepository;
    private final InvitadoRepository invitadoRepository;
    private final UsuarioControlRepository usuarioControlRepository;
 

    @Override
    @Transactional(readOnly = true)
    public List<ComentarioDTO> obtenerTodosLosComentarios() {
        return comentarioRepository.findAll().stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ComentarioDTO obtenerComentarioPorId(Long id) {
        Comentario comentario = comentarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comentario no encontrado con ID: " + id));
        return convertToDTO(comentario);
    }

    @Override
    public ComentarioDTO crearComentario(@Valid ComentarioDTO dto) {
        comentarioValidator.validarComentario(dto);

        Persona persona = buscarPersonaPorId(dto.getIdPersona());

        Cancha cancha = canchaRepository.findById(dto.getIdCancha())
                .orElseThrow(() -> new RuntimeException("Cancha no encontrada con ID: " + dto.getIdCancha()));

        Comentario entidad = toEntity(dto, persona, cancha);
        entidad.setIdComentario(null);
        entidad.setEstado(Boolean.TRUE);

        return convertToDTO(comentarioRepository.save(entidad));
    }

    @Override
    public ComentarioDTO actualizarComentario(Long id, @Valid ComentarioDTO dto) {
        Comentario existente = comentarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comentario no encontrado con ID: " + id));

        comentarioValidator.validarComentario(dto);

        Persona persona = buscarPersonaPorId(dto.getIdPersona());

        Cancha cancha = canchaRepository.findById(dto.getIdCancha())
                .orElseThrow(() -> new RuntimeException("Cancha no encontrada con ID: " + dto.getIdCancha()));

        existente.setContenido(dto.getContenido());
        existente.setCalificacion(dto.getCalificacion());
        existente.setFecha(dto.getFecha());
        existente.setEstado(dto.getEstado());
        existente.setPersona(persona);
        existente.setCancha(cancha);
        Comentario actualizado = comentarioRepository.save(existente);
        
        return convertToDTO(actualizado);
    }

    @Override
    public ComentarioDTO eliminarComentario(Long id) {
        Comentario existente = comentarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comentario no encontrado con ID: " + id));
        existente.setEstado(Boolean.FALSE);
        return convertToDTO(comentarioRepository.save(existente));
    }

    @Override
    public void eliminarComentarioFisicamente(Long id) {
        Comentario existente = comentarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comentario no encontrado con ID: " + id));
        comentarioRepository.delete(existente);
    }

    @Override
    public Comentario obtenerComentarioConBloqueo(Long id) {
        Comentario comentario = comentarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comentario no encontrado con ID: " + id));
        try {
            Thread.sleep(15000); // Simula espera
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return comentario;
    }


    @Override
    @Transactional(readOnly = true)
    public List<ComentarioDTO> getComentariosPorCancha(Long canchaId) {
        return comentarioRepository.findByCancha_IdCancha(canchaId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
     // ---------- COMENTARIOS POR CANCHA ----------
    /*@Override
    @Transactional(readOnly = true)
    public List<ComentarioDTO> getComentariosPorCancha(Long canchaId) {
        return comentarioRepository.findByCancha_IdCanchaOrderByFechaDesc(canchaId).stream()
                .map(this::convertToDTO)
                .toList();
    }*/

    // ---------- COMENTARIOS MÁS RECIENTES ----------
    @Transactional(readOnly = true)
    public List<ComentarioDTO> getComentariosMasRecientes(int limite) {  // ✅ AGREGAR parámetro
        return comentarioRepository.findAllByOrderByFechaDesc().stream()
                .limit(limite)  // ✅ AGREGAR límite
                .map(this::convertToDTO)
                .toList();
    }

    // ---------- COMENTARIOS CON MAYOR PUNTUACIÓN ----------
    @Transactional(readOnly = true)
    public List<ComentarioDTO> getComentariosMayorCalificacion(int limite) {  // ✅ AGREGAR parámetro
        return comentarioRepository.findByOrderByCalificacionDesc().stream()
                .limit(limite)  // ✅ AGREGAR límite
                .map(this::convertToDTO)
                .toList();
    }

    // ---------- COMENTARIOS CON MAYOR PUNTUACIÓN Y RECIENTES ----------
    @Transactional(readOnly = true)
    public List<ComentarioDTO> getComentariosMayorCalificacionMasRecientes(int limite) {  // ✅ AGREGAR parámetro
        return comentarioRepository.findMayorCalificacionMasRecientes().stream()
                .limit(limite)  // ✅ AGREGAR límite
                .map(this::convertToDTO)
                .toList();
    }

    // ---------- COMENTARIOS POR CALIFICACIÓN ESPECÍFICA ----------
    @Transactional(readOnly = true)
    public List<ComentarioDTO> getComentariosPorCalificacion(int calificacion) {
        return comentarioRepository.findByCalificacion(calificacion).stream()
                .map(this::convertToDTO)
                .toList();
    }

    // ---------- COMENTARIOS POR CLIENTE ----------
    @Transactional(readOnly = true)
    public List<ComentarioDTO> getComentariosPorCliente(Long clienteId) {
        return comentarioRepository.findByPersona_Id(clienteId).stream()
                .map(this::convertToDTO)
                .toList();
    }

    // ---------- COMENTARIOS POR ÁREA DEPORTIVA ----------
    @Transactional(readOnly = true)
    public List<ComentarioDTO> getComentariosPorAreaDeportiva(Long areaId) {
        return comentarioRepository.findByAreaDeportiva(areaId).stream()
                .map(this::convertToDTO)
                .toList();
    }

    

    // ---------- mapping ----------
    private ComentarioDTO convertToDTO(Comentario c) {
        Persona p = c.getPersona();

        PersonaPublicaDTO personaDTO = null;
        if (p != null) {
            personaDTO = PersonaPublicaDTO.builder()
                .id(p.getId())
                .nombre(p.getNombre())
                .apellidoPaterno(p.getApellidoPaterno())
                .apellidoMaterno(p.getApellidoMaterno())
                .urlImagen(p.getUrlImagen())
                .build();
        }

        return ComentarioDTO.builder()
                .idComentario(c.getIdComentario())
                .contenido(c.getContenido())
                .calificacion(c.getCalificacion())
                .fecha(c.getFecha())
                .estado(c.getEstado())
                .idPersona(p != null ? p.getId() : null)
                .idCancha(c.getCancha() != null ? c.getCancha().getIdCancha() : null)
                .persona(personaDTO) // ✅ limpio, sin recursión
                .build();
    }

    private Comentario toEntity(ComentarioDTO d, Persona persona, Cancha cancha) {
        return Comentario.builder()
                .idComentario(d.getIdComentario())
                .contenido(d.getContenido())
                .calificacion(d.getCalificacion())
                .fecha(d.getFecha() != null ? d.getFecha() : LocalDateTime.now())
                .estado(d.getEstado() != null ? d.getEstado() : Boolean.TRUE)
                .persona(persona)
                .cancha(cancha)
                .build();
    }

    // ---------- persona resolver interno ----------
    private Persona buscarPersonaPorId(Long id) {
        return Stream.of(
                administradorRepository.findById(id),
                clienteRepository.findById(id),
                invitadoRepository.findById(id),
                usuarioControlRepository.findById(id)
        ).filter(Optional::isPresent)
         .map(Optional::get)
         .findFirst()
         .orElseThrow(() -> new RuntimeException("No se encontró ninguna persona con ID: " + id));
    }
}
