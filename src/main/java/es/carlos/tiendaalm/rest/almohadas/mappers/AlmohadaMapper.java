package es.carlos.tiendaalm.rest.almohadas.mappers;

import es.carlos.tiendaalm.rest.almohadas.dto.*;
import es.carlos.tiendaalm.rest.almohadas.models.*;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AlmohadaMapper {
    public Almohada toAlmohada(AlmohadaCreateDto almohada){
        return Almohada.builder().
                id(null).
                peso(almohada.getPeso()).
                altura(almohada.getAltura()).
                ancho(almohada.getAncho()).
                grosor(almohada.getGrosor()).
                tacto(almohada.getTacto()).
                firmeza(almohada.getFirmeza()).
                build();
    }

    public Almohada toAlmohada(AlmohadaUpdateDto almohadaUpdt, Almohada almohada){
        return Almohada.builder().
            id(almohada.getId()).
            peso(almohadaUpdt.getPeso() != null ? almohadaUpdt.getPeso() : almohada.getPeso()).
            altura(almohadaUpdt.getAltura() != null ? almohadaUpdt.getAltura() : almohada.getAltura()).
            ancho(almohadaUpdt.getAncho() != null ? almohadaUpdt.getAncho() : almohada.getAncho()).
            grosor(almohadaUpdt.getGrosor() != null ? almohadaUpdt.getGrosor() : almohada.getGrosor()).
            tacto(almohadaUpdt.getTacto() != null ? almohadaUpdt.getTacto() : almohada.getTacto()).
            firmeza(almohadaUpdt.getFirmeza() != null ? almohadaUpdt.getFirmeza() : almohada.getFirmeza()).
            build();
    }

    public AlmohadaResponseDto toAlmohadaResponseDto(Almohada almohada){
        return AlmohadaResponseDto.builder().
                id(almohada.getId()).
                peso(almohada.getPeso()).
                altura(almohada.getAltura()).
                ancho(almohada.getAncho()).
                grosor(almohada.getGrosor()).
                tacto(almohada.getTacto()).
                firmeza(almohada.getFirmeza()).
                build();
    }

    public List<AlmohadaResponseDto> toResponseDtoList(List<Almohada> almohadas) {
        return almohadas.stream()
                .map(this::toAlmohadaResponseDto)
                .toList();
    }

    public Page<AlmohadaResponseDto> toResponseDtoPage(Page<Almohada> almohadas) {
        return almohadas.map(this::toAlmohadaResponseDto);
    }
}
