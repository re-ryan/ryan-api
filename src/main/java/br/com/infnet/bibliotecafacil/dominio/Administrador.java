package br.com.infnet.bibliotecafacil.dominio;

public final class Administrador extends Usuario {

    @Override
    public String toString() {
        return "Administrador{%s}".formatted(this.descreverUsuario());
    }
}
