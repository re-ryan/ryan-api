package br.com.infnet.bibliotecafacil.dominio;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("ADMINISTRADOR")
public class Administrador extends Usuario {

    @Override
    public String toString() {
        return "Administrador{%s}".formatted(this.descreverUsuario());
    }
}
