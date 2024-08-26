package br.insper.aposta.aposta;

public class JogoNaoEncontradoException extends RuntimeException {
    public JogoNaoEncontradoException(String message) {
        super(message);
    }
}