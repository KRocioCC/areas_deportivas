package com.espaciosdeportivos.service.impl;

import com.espaciosdeportivos.dto.AreaDeportivaDTO;
import com.espaciosdeportivos.dto.MacrodistritoDTO;
//import com.espaciosdeportivos.dto.CanchaDTO;
import com.espaciosdeportivos.dto.ZonaDTO; // objeto front K
import com.espaciosdeportivos.model.AreaDeportiva;
import com.espaciosdeportivos.model.Macrodistrito;
//import com.espaciosdeportivos.model.Cancha;
import com.espaciosdeportivos.model.Zona;
import com.espaciosdeportivos.model.Administrador;

import com.espaciosdeportivos.repository.AreaDeportivaRepository;
import com.espaciosdeportivos.repository.ZonaRepository;
import com.espaciosdeportivos.repository.AdministradorRepository;

import com.espaciosdeportivos.service.IAreaDeportivaService;
import com.espaciosdeportivos.service.ImagenService;
import com.espaciosdeportivos.repository.DisciplinaRepository;
import com.espaciosdeportivos.model.Disciplina;
import com.espaciosdeportivos.dto.DisciplinaDTO;
import com.espaciosdeportivos.validation.AreaDeportivaValidator;

//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AreaDeportivaServiceImpl implements IAreaDeportivaService {

    private final AreaDeportivaRepository areaDeportivaRepository;
    private final ZonaRepository zonaRepository;
    private final AdministradorRepository administradorRepository;
    private final AreaDeportivaValidator areaDeportivaValidator;

    private final ImagenService imagenService;
    private final DisciplinaRepository disciplinaRepository;
    private static final String ENTIDAD_TIPO = "AREADEPORTIVA";


    /*@Autowired
    public AreaDeportivaServiceImpl(
        AreaDeportivaRepository areaDeportivaRepository, 
        ZonaRepository zonaRepository, 
        AdministradorRepository administradorRepository, 
        AreaDeportivaValidator areaDeportivaValidator,
        ImagenService imagenService
    ) {
        this.areaDeportivaRepository = areaDeportivaRepository;
        this.zonaRepository = zonaRepository;
        this.administradorRepository = administradorRepository;
        this.areaDeportivaValidator = areaDeportivaValidator;
        this.imagenService = imagenService;
    }*/

    @Override
    @Transactional(readOnly = true)
    public List<AreaDeportivaDTO> listarTodos() {
        return areaDeportivaRepository.findByEstadoTrue()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AreaDeportivaDTO> obtenerTodasLasAreasDeportivas() {
        return areaDeportivaRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public AreaDeportivaDTO obtenerAreaDeportivaPorId(Long id) {
        AreaDeportiva area = areaDeportivaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Área deportiva no encontrada con ID: " + id));
        return convertToDTO(area);
    }

    @Override
    public AreaDeportivaDTO crearAreaDeportiva(AreaDeportivaDTO areaDTO) {
        areaDeportivaValidator.validarArea(areaDTO);

        boolean existeAdministrador = administradorRepository.existsById(areaDTO.getId());
        if (!existeAdministrador) {
            throw new EntityNotFoundException("El Administrador con ID " + areaDTO.getId() + " no existe.");
        }

        AreaDeportiva area = convertToEntity(areaDTO);
        area.setIdAreaDeportiva(null);
        area.setEstado(Boolean.TRUE);

        AreaDeportiva guardada = areaDeportivaRepository.save(area);
        return convertToDTO(guardada);
    }

    @Override
    public AreaDeportivaDTO actualizarAreaDeportiva(Long id, @Valid AreaDeportivaDTO dto) {
        AreaDeportiva existente = areaDeportivaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Área deportiva no encontrada con ID: " + id));
        areaDeportivaValidator.validarArea(dto);

        Zona zona = zonaRepository.findById(dto.getIdZona())
                .orElseThrow(() -> new RuntimeException("Zona no encontrada con ID: " + dto.getIdZona()));
        Administrador admin = administradorRepository.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("Administrador no encontrado con ID: " + dto.getId()));

        existente.setNombreArea(dto.getNombreArea());
        existente.setDescripcionArea(dto.getDescripcionArea());
        existente.setEmailArea(dto.getEmailArea());
        existente.setTelefonoArea(dto.getTelefonoArea());
        existente.setHoraInicioArea(dto.getHoraInicioArea() != null ? dto.getHoraInicioArea() : null);
        existente.setHoraFinArea(dto.getHoraFinArea() != null ? dto.getHoraFinArea(): null);
        existente.setUrlImagen(dto.getUrlImagen());
        existente.setLatitud(dto.getLatitud());
        existente.setLongitud(dto.getLongitud());
        existente.setEstado(dto.getEstado());
        existente.setZona(zona);
        existente.setAdministrador(admin);

        AreaDeportiva actualizada = areaDeportivaRepository.save(existente);
        return convertToDTO(actualizada);
    }

    @Override
    @Transactional
    public void eliminarAreaDeportivaFisicamente(Long id) {
        AreaDeportiva existente = areaDeportivaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Área deportiva no encontrada con ID: " + id));
        areaDeportivaRepository.delete(existente);
    }

    @Override
    @Transactional
    public AreaDeportivaDTO eliminarAreaDeportiva(Long idarea , Boolean nuevoEstado) {
        AreaDeportiva existente = areaDeportivaRepository.findByIdAreaDeportivaAndEstadoTrue(idarea)
                .orElseThrow(() -> new RuntimeException("Área deportiva no encontrada con ID: " + idarea));
        existente.setEstado(nuevoEstado);
        return convertToDTO(areaDeportivaRepository.save(existente));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AreaDeportivaDTO> buscarPorNombre(String nombre) {
        return areaDeportivaRepository.buscarPorNombre(nombre)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AreaDeportiva obtenerAreaDeportivaConBloqueo(Long id) {
        AreaDeportiva areaDeportiva = areaDeportivaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Área deportiva no encontrada con ID: " + id));
        try {
            Thread.sleep(15000); 
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return areaDeportiva;
    }

         // objeto front K
    /*private ZonaDTO convertZonaToDTO(Zona z) {
        if (z == null) return null;
        return ZonaDTO.builder()
                .idZona(z.getIdZona()) // objeto front K
                .nombre(z.getNombre()) // objeto front K
                .descripcion(z.getDescripcion()) // objeto front K
                .estado(z.getEstado()) // objeto front K
                .idMacrodistrito(z.getMacrodistrito() != null ? z.getMacrodistrito().getIdMacrodistrito() : null) // objeto front K
                .build();
    }*/

     // ==========================================================
// 🖼️ MÉTODOS DE GESTIÓN DE IMÁGENES PARA CANCHAS
// ==========================================================

    @Override
    @Transactional
    public AreaDeportivaDTO agregarImagenes(Long idAreadeportiva, List<MultipartFile> archivosImagenes) {
        log.info("📸 Agregando {} imágenes a la area ID: {}", archivosImagenes.size(), idAreadeportiva);

        AreaDeportiva area = areaDeportivaRepository.findByIdAreaDeportivaAndEstadoTrue(idAreadeportiva)
                .orElseThrow(() -> new RuntimeException("area no encontrada o inactiva"));

        imagenService.guardarImagenesParaEntidad(archivosImagenes, ENTIDAD_TIPO, idAreadeportiva);
        log.info("Imágenes agregadas exitosamente a la area {}", idAreadeportiva);

        return obtenerAreaDeportivaPorId(idAreadeportiva);
    }

    @Override
    @Transactional
    public AreaDeportivaDTO eliminarImagen(Long idAreadeportiva, Long idImagenRelacion) {
        log.info("Eliminando imagen {} de la area {}", idImagenRelacion, idAreadeportiva);

        areaDeportivaRepository.findByIdAreaDeportivaAndEstadoTrue(idAreadeportiva)
                .orElseThrow(() -> new RuntimeException("area no encontrada o inactiva"));

        imagenService.eliminarImagenLogicamente(idImagenRelacion);
        log.info("Imagen eliminada correctamente");

        return obtenerAreaDeportivaPorId(idAreadeportiva);
    }

    @Override
    @Transactional
    public AreaDeportivaDTO reordenarImagenes(Long idAreadeportiva, List<Long> idsImagenesOrden) {
        log.info("Reordenando {} imágenes de la area {}", idsImagenesOrden.size(), idAreadeportiva);

        areaDeportivaRepository.findByIdAreaDeportivaAndEstadoTrue(idAreadeportiva)
                .orElseThrow(() -> new RuntimeException("area no encontrada o inactiva"));

        imagenService.reordenarImagenes(ENTIDAD_TIPO, idAreadeportiva, idsImagenesOrden);
        log.info("Imágenes reordenadas con éxito");

        return obtenerAreaDeportivaPorId(idAreadeportiva);
    }


    // ---------- mapping ----------
    private AreaDeportivaDTO convertToDTO(AreaDeportiva a) {
        AreaDeportivaDTO dto = AreaDeportivaDTO.builder()
                .idAreadeportiva(a.getIdAreaDeportiva())
                .nombreArea(a.getNombreArea())
                .descripcionArea(a.getDescripcionArea())
                .emailArea(a.getEmailArea())
                .telefonoArea(a.getTelefonoArea())
                .horaInicioArea(a.getHoraInicioArea())
                .horaFinArea(a.getHoraFinArea())
                .urlImagen(a.getUrlImagen())
                .latitud(a.getLatitud())
                .longitud(a.getLongitud())
                .estado(a.getEstado())
                .idZona(a.getZona() != null ? a.getZona().getIdZona() : null)
                .zona(convertZonaToDTO(a.getZona())) // objeto front K
                .id(a.getAdministrador() != null ? a.getAdministrador().getId() : null)
                .build();

        // Cargar imágenes asociadas a la área
        try {
            List<com.espaciosdeportivos.dto.ImagenDTO> imagenes = imagenService.obtenerImagenesPorEntidad(ENTIDAD_TIPO, a.getIdAreaDeportiva());
            dto.setImagenes(imagenes);
        } catch (Exception e) {
            log.warn("Error cargando imágenes para área {}: {}", a.getIdAreaDeportiva(), e.getMessage());
            dto.setImagenes(java.util.List.of());
        }

        return dto;
    }


    private AreaDeportiva convertToEntity(AreaDeportivaDTO d) {
        Administrador administrador = administradorRepository.findById(d.getId())
                .orElseThrow(() -> new RuntimeException("Admin no encontrada con ID: " + d.getId()));        
        Zona zona = zonaRepository.findById(d.getIdZona())
                .orElseThrow(() -> new RuntimeException("Zona no encontrada con ID: " + d.getIdZona()));

        return AreaDeportiva.builder()
                .idAreaDeportiva(d.getIdAreadeportiva())
                .nombreArea(d.getNombreArea())
                .descripcionArea(d.getDescripcionArea())
                .emailArea(d.getEmailArea())
                .telefonoArea(d.getTelefonoArea())
                .horaInicioArea(d.getHoraInicioArea() != null ? d.getHoraInicioArea(): null)
                .horaFinArea(d.getHoraFinArea() != null ? d.getHoraFinArea() : null)
                .urlImagen(d.getUrlImagen())
                .latitud(d.getLatitud())
                .longitud(d.getLongitud())
                .estado(d.getEstado() != null ? d.getEstado() : Boolean.TRUE)
                .zona(zona)
                .administrador(administrador)
                .build();
    }

    private LocalTime parseTime(String t) {
        return (t != null && !t.isBlank()) ? LocalTime.parse(t) : null;
    }

     // objeto front 
    private ZonaDTO convertZonaToDTO(Zona z) {
        if (z == null) return null;
        return ZonaDTO.builder()
                .idZona(z.getIdZona()) // objeto front K
                .nombre(z.getNombre()) // objeto front K
                .descripcion(z.getDescripcion()) // objeto front K
                .estado(z.getEstado()) // objeto front K
                .idMacrodistrito(z.getMacrodistrito() != null ? z.getMacrodistrito().getIdMacrodistrito() : null) // objeto front K
                .macrodistrito(convertMacrodistritoToDTO(z.getMacrodistrito()))
                .build();
    }

    private MacrodistritoDTO convertMacrodistritoToDTO(Macrodistrito z) {
        if (z == null) return null;
        return MacrodistritoDTO.builder()
                .idMacrodistrito(z.getIdMacrodistrito())
                .nombre(z.getNombre())
                .descripcion(z.getDescripcion())
                .estado(z.getEstado())
                .build();
    }

    // ------------------ Disciplina DTO converter ------------------
    private com.espaciosdeportivos.dto.DisciplinaDTO convertDisciplinaToDTO(Disciplina disciplina) {
        com.espaciosdeportivos.dto.DisciplinaDTO dto = com.espaciosdeportivos.dto.DisciplinaDTO.builder()
                .idDisciplina(disciplina.getIdDisciplina())
                .nombre(disciplina.getNombre())
                .descripcion(disciplina.getDescripcion())
                .estado(disciplina.getEstado())
                .fechaCreacion(disciplina.getFechaCreacion())
                .fechaActualizacion(disciplina.getFechaActualizacion())
                .build();

        try {
            java.util.List<com.espaciosdeportivos.dto.ImagenDTO> imagenes = imagenService.obtenerImagenesPorEntidad("DISCIPLINA", disciplina.getIdDisciplina());
            dto.setImagenes(imagenes);
        } catch (Exception e) {
            dto.setImagenes(java.util.List.of());
            log.warn("Error cargando imágenes para disciplina {}: {}", disciplina.getIdDisciplina(), e.getMessage());
        }

        return dto;
    }

    @Override
    public com.espaciosdeportivos.dto.DisciplinaDTO crearDisciplinaPorAdmin(Long adminId, com.espaciosdeportivos.dto.DisciplinaDTO dto) {
        AreaDeportiva area = areaDeportivaRepository.findByAdministrador_Id(adminId)
                .orElseThrow(() -> new RuntimeException("Área no encontrada para el administrador"));

        if (dto.getNombre() == null || dto.getNombre().isBlank()) {
            throw new RuntimeException("El nombre de la disciplina es obligatorio");
        }


        Disciplina disciplina = Disciplina.builder()
                .nombre(dto.getNombre())
                .descripcion(dto.getDescripcion())
                .estado(dto.getEstado() != null ? dto.getEstado() : Boolean.TRUE)
                .areaDeportiva(area)
                .build();

        disciplina = disciplinaRepository.save(disciplina);
        return convertDisciplinaToDTO(disciplina);
    }

    @Override
    @Transactional(readOnly = true)
    public java.util.List<com.espaciosdeportivos.dto.DisciplinaDTO> listarDisciplinasPorAdmin(Long adminId) {
        java.util.List<Disciplina> disciplinas = disciplinaRepository.findDisciplinasByAdminId(adminId);
        return disciplinas.stream().map(this::convertDisciplinaToDTO).collect(java.util.stream.Collectors.toList());
    }

    @Override
    public com.espaciosdeportivos.dto.DisciplinaDTO actualizarDisciplinaPorAdmin(Long adminId, Long idDisciplina, com.espaciosdeportivos.dto.DisciplinaDTO dto) {
        Disciplina d = disciplinaRepository.findById(idDisciplina)
                .orElseThrow(() -> new RuntimeException("Disciplina no encontrada: " + idDisciplina));

        if (d.getAreaDeportiva() == null || d.getAreaDeportiva().getAdministrador() == null ||
                !d.getAreaDeportiva().getAdministrador().getId().equals(adminId)) {
            throw new RuntimeException("La disciplina no pertenece al área del administrador");
        }

        d.setNombre(dto.getNombre());
        d.setDescripcion(dto.getDescripcion());
        d.setEstado(dto.getEstado() != null ? dto.getEstado() : d.getEstado());

        Disciplina saved = disciplinaRepository.save(d);
        return convertDisciplinaToDTO(saved);
    }

    @Override
    public void eliminarDisciplinaPorAdmin(Long adminId, Long idDisciplina) {
        Disciplina d = disciplinaRepository.findById(idDisciplina)
                .orElseThrow(() -> new RuntimeException("Disciplina no encontrada: " + idDisciplina));

        if (d.getAreaDeportiva() == null || d.getAreaDeportiva().getAdministrador() == null ||
                !d.getAreaDeportiva().getAdministrador().getId().equals(adminId)) {
            throw new RuntimeException("La disciplina no pertenece al área del administrador");
        }

        d.setEstado(Boolean.FALSE);
        disciplinaRepository.save(d);
    }

    @Override
   public AreaDeportivaDTO obtenerPorAdminId(Long id) {
    Optional<AreaDeportiva> areaOpt = areaDeportivaRepository.findByAdministrador_Id(id);

    if (areaOpt.isEmpty()) {
        return null; //  devolvemos null para que el controlador maneje el 404
    }

    return convertToDTO(areaOpt.get());
}


    //MI_AREA k actualizar por adminId   
    //Comentario : "rocio cambien las fechas ya no sons tring espero que no te afecte mucho perdon"
    @Override
    public AreaDeportivaDTO actualizarPorAdminId(Long adminId, AreaDeportivaDTO dto) {
        AreaDeportiva area = areaDeportivaRepository.findByAdministrador_Id(adminId)
            .orElseThrow(() -> new RuntimeException("Área no encontrada para el administrador"));

        areaDeportivaValidator.validarArea(dto); // validación como en otros métodos

        Zona zona = zonaRepository.findById(dto.getIdZona())
            .orElseThrow(() -> new RuntimeException("Zona no encontrada con ID: " + dto.getIdZona()));

        // Actualiza los campos permitidos
        area.setNombreArea(dto.getNombreArea());
        area.setDescripcionArea(dto.getDescripcionArea());
        area.setEmailArea(dto.getEmailArea());
        area.setTelefonoArea(dto.getTelefonoArea());
        area.setHoraInicioArea(dto.getHoraInicioArea() != null ? dto.getHoraInicioArea(): null);
        area.setHoraFinArea(dto.getHoraFinArea() != null ? dto.getHoraFinArea() : null);
        area.setUrlImagen(dto.getUrlImagen());
        area.setLatitud(dto.getLatitud());
        area.setLongitud(dto.getLongitud());
        area.setEstado(dto.getEstado());
        area.setZona(zona);

        AreaDeportiva actualizada = areaDeportivaRepository.save(area);
        return convertToDTO(actualizada);
    }





}
