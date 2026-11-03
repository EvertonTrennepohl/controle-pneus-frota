package com.frotapneus.infra.exception;

public record DadosErroAutenticacao(
	int status,
	String erro,
	String mensagem) {

}
