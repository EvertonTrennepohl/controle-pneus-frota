package com.gestaofrota.infra.exception;

public record DadosErroAutenticacao(
	int status,
	String erro,
	String mensagem) {

}
