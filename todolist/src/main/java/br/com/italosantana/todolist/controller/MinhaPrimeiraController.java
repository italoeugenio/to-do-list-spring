package br.com.italosantana.todolist.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/primeiraRota")
public class MinhaPrimeiraController {

/* Métodos de acesso do HTTP
* GET - Buscar informação
* POST - Adicionar um dado/informação
* PUT - Alterar um dado/info
* DELETE - Remove um dado
* PATCH - Alterar somente uma parte da info/dado
* */
    @GetMapping("/")
    public String primeiraMensagem(){
        return "Funcionou";
    }
}
