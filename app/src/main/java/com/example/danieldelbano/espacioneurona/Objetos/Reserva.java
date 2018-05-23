package com.example.danieldelbano.espacioneurona.Objetos;

import java.io.Serializable;
import java.util.ArrayList;

public class Reserva implements Serializable{
    private String nombre;
    private String emailUsuario;
    private String fecha;
    private String lugar;
    private ArrayList<String>hora;
    private String horario;
    private String complementos;

    public Reserva() {

    }

    public Reserva(String nombre, String emailUsuario, String fecha,String lugar) {
        this.nombre = nombre;
        this.emailUsuario = emailUsuario;
        this.fecha = fecha;
        this.lugar = lugar;
    }

    public Reserva(String nombre, String emailUsuario, String fecha,String lugar,String complementos) {
        this.nombre = nombre;
        this.emailUsuario = emailUsuario;
        this.fecha = fecha;
        this.lugar = lugar;
        this.complementos = complementos;
        this.hora=new ArrayList<>();
    }


    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getEmailUsuario() {
        return emailUsuario;
    }

    public void setEmailUsuario(String emailUsuario) {
        this.emailUsuario = emailUsuario;
    }

    public String getLugar() {
        return lugar;
    }

    public void setLugar(String lugar) {
        this.lugar = lugar;
    }

    public String getComplementos() {
        return complementos;
    }

    public void setComplementos(String complementos) {
        this.complementos = complementos;
    }

    public ArrayList<String> getHora() {
        return hora;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    public void setHora(ArrayList<String> hora) {
        this.hora = hora;
    }

    @Override
    public String toString() {
        horario="";
        if (hora!=null){
            for(String s:hora){
                horario+=s + " / ";
            }
        }else{
            horario="";
        }

        return "Reserva:" +
                "Nombre='" + nombre + '\'' +
                ", Email usuario='" + emailUsuario + '\'' +
                ", Fecha='" + fecha + '\'' +
                ", Lugar='" + lugar + '\'' +
                ", Hora='" + horario + '\'' +
                ", Complementos='" + complementos + '\'' +
                '.';
    }

}
