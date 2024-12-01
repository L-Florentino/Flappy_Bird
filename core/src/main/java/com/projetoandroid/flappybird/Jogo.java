package com.projetoandroid.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Jogo extends ApplicationAdapter {

    private  int movimentoX = 0;
    private  int movimentoY = 0;
    private SpriteBatch batch;
    private Texture[] passaros;
    private Texture fundo;

    // Atributos de configurações
    private float larguraDispositivo;
    private float alturaDispositivo;
    private float variacao = 0;
    private float gravidade = 0;
    private float posicaoPassaroY = 0;

    @Override
    public void create() {
        //Gdx.app.log("create", "Jogo iniciado");
        batch = new SpriteBatch();
        passaros = new Texture[3];
        passaros[0] = new Texture("passaro1.png");
        passaros[1] = new Texture("passaro2.png");
        passaros[2] = new Texture("passaro3.png");
        fundo = new Texture("fundo.png");

        larguraDispositivo = Gdx.graphics.getWidth();
        alturaDispositivo = Gdx.graphics.getHeight();
        posicaoPassaroY = alturaDispositivo / 2;


    }

    @Override
    public void render() {

        batch.begin();
        /*
        * 0,1,2, 3
        * */
        if (variacao > 3)
            variacao = 0;

        // Aplicar evento de toque na tela
        // Aplicar a gravidade no pássaro
        if (posicaoPassaroY > 0)
            posicaoPassaroY = posicaoPassaroY - gravidade;

        batch.draw(fundo, 0, 0, larguraDispositivo, alturaDispositivo);
        batch.draw( passaros[(int) variacao], 30, posicaoPassaroY);

        variacao += Gdx.graphics.getDeltaTime() * 10;

        gravidade ++;
        movimentoX ++;
        movimentoY ++;
        batch.end();

        /*
        contador++;
        Gdx.app.log("render", "Jogo renderizado: " + contador);
        */
    }

    @Override
    public void dispose() {
        Gdx.app.log("dispose", "Descarte de conteúdos");
    }
}