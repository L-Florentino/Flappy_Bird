package com.projetoandroid.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Random;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Jogo extends ApplicationAdapter {

    //Texturas
    private SpriteBatch batch;
    private Texture[] passaros;
    private Texture fundo;
    private Texture canoBaixo;
    private Texture canoTopo;
    private Texture gameOver;

    // Formas para colisão
    private ShapeRenderer shapeRenderer;
    private Circle circuloPassaro;
    private Rectangle retanguloCanoTopo;
    private Rectangle retanguloCanoBaixo;

    // Atributos de configurações
    private float larguraDispositivo;
    private float alturaDispositivo;
    private float variacao = 0;
    private float gravidade = 1;
    private float posicaoPassaroY = 0;
    private float posicaoCanoHorizontal = 0;
    private float posicaoCanoVertical;
    private float espacoEntreCanos;
    private Random random;
    private int pontos = 0;
    private int pontuacaoMaxima = 0;
    private boolean passouCano = false;
    private int estadoJogo = 0;
    private float posicaoPassaroX = 0;

    // Exibição de textos
    BitmapFont textoPontuacao;
    BitmapFont textoReiniciair;
    BitmapFont textoMelhorPontuacao;

    // Configurar sons
    Sound somVoando;
    Sound somColisao;
    Sound somPontuacao;

    // Objeto salvar pontuação
    Preferences preferencias;

    // Objetos para câmera
    private OrthographicCamera camera;
    private Viewport viewport;
    private final float VIRTUAL_WIDTH = 720;
    private final float VIRTUAL_HEIGHT = 1280;

    @Override
    public void create() {
        inicializarTexturas();
        inicializarObjetos();
    }

    @Override
    public void render() {

        // Limpar frames anteriores
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        verificarEstadoDoJogo();
        validarPontos();
        desenharTexturas();
        detectarColisoes();
    }

    private void verificarEstadoDoJogo(){

        boolean toqueTela = Gdx.input.justTouched();

        if (estadoJogo == 0) {

            // Aplicar evento de toque na tela
            if (toqueTela){
                gravidade = -15;
                estadoJogo = 1;
                somVoando.play();
            }

        } else if (estadoJogo == 1) {

            // Aplicar evento de toque na tela
            if (toqueTela){
                gravidade = -15;
                somVoando.play();
            }

            //Movimentar o cano
            posicaoCanoHorizontal -= Gdx.graphics.getDeltaTime() * 400;
            if (posicaoCanoHorizontal < - canoTopo.getWidth()){
                posicaoCanoHorizontal = larguraDispositivo;
                posicaoCanoVertical = random.nextInt(400) - 200;
                passouCano = false;
            }

            // Aplicar a gravidade no pássaro
            if (posicaoPassaroY > 0 || toqueTela)
                posicaoPassaroY = posicaoPassaroY - gravidade;

            gravidade ++;

        } else if (estadoJogo == 2) {

            // Aplicar a gravidade no pássaro
            /*if (posicaoPassaroY > 0 || toqueTela)
                posicaoPassaroY = posicaoPassaroY - gravidade;
            gravidade ++;*/

            //
            if (pontos > pontuacaoMaxima){
                pontuacaoMaxima = pontos;
                preferencias.putInteger("pontuacaoMaxima", pontuacaoMaxima);
            }

            posicaoPassaroX -= Gdx.graphics.getDeltaTime() * 500;

            // Aplicar evento de toque na tela
            if (toqueTela){
                estadoJogo = 0;
                pontos = 0;
                gravidade = 0;
                posicaoPassaroX = 0;
                posicaoPassaroY = alturaDispositivo / 2;
                posicaoCanoHorizontal = larguraDispositivo;
            }

        }
    }

    private void detectarColisoes(){

        circuloPassaro.set(
            50 + posicaoPassaroX + passaros[0].getWidth() / 2, posicaoPassaroY + passaros[0].getHeight() / 2, passaros[0].getWidth() / 2
        );

        retanguloCanoBaixo.set(
            posicaoCanoHorizontal, alturaDispositivo / 2 - canoBaixo.getHeight() - espacoEntreCanos / 2 + posicaoCanoVertical,
            canoBaixo.getWidth(), canoBaixo.getHeight()
        );

        retanguloCanoTopo.set(
            posicaoCanoHorizontal, alturaDispositivo / 2 + espacoEntreCanos / 2 +  posicaoCanoVertical,
            canoTopo.getWidth(), canoTopo.getHeight()
        );

        boolean colidiuCanoTopo = Intersector.overlaps(circuloPassaro, retanguloCanoTopo);
        boolean colidiuCanoBaixo = Intersector.overlaps(circuloPassaro, retanguloCanoBaixo);

        if ( colidiuCanoTopo || colidiuCanoBaixo ){
            if (estadoJogo == 1) {
                somColisao.play();
                estadoJogo = 2;
            }
        }

        /*
        // Define as formas para vizualizar colisões
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.BLUE);

        shapeRenderer.circle(50 + passaros[0].getWidth() / 2, posicaoPassaroY + passaros[0].getHeight() / 2, passaros[0].getWidth() / 2);

        // Topo
        shapeRenderer.rect(
            posicaoCanoHorizontal, alturaDispositivo / 2 + espacoEntreCanos / 2 +  posicaoCanoVertical,
            canoTopo.getWidth(), canoTopo.getHeight()
        );

        // Baixo
        shapeRenderer.rect(
            posicaoCanoHorizontal, alturaDispositivo / 2 - canoBaixo.getHeight() - espacoEntreCanos / 2 + posicaoCanoVertical,
            canoBaixo.getWidth(), canoBaixo.getHeight()
        );

        shapeRenderer.end();
        */
    }

    private  void desenharTexturas(){

        batch.setProjectionMatrix(camera.combined);

        batch.begin();

        batch.draw(fundo, 0, 0, larguraDispositivo, alturaDispositivo);
        batch.draw( passaros[(int) variacao], 50 + posicaoPassaroX, posicaoPassaroY);
        batch.draw(canoBaixo, posicaoCanoHorizontal, alturaDispositivo / 2 - canoBaixo.getHeight() - espacoEntreCanos / 2 + posicaoCanoVertical);
        batch.draw(canoTopo, posicaoCanoHorizontal, alturaDispositivo / 2 + espacoEntreCanos / 2 +  posicaoCanoVertical);
        textoPontuacao.draw(batch, String.valueOf(pontos), larguraDispositivo / 2, alturaDispositivo - 110);

        if (estadoJogo == 2){
            batch.draw(gameOver, larguraDispositivo / 2 - gameOver.getWidth() / 2, alturaDispositivo / 2);
            textoReiniciair.draw(batch, "Toque para reiniciar", larguraDispositivo / 2 - 140, alturaDispositivo / 2 - gameOver.getHeight() / 2);
            textoMelhorPontuacao.draw(batch, "Seu record é: "+ pontuacaoMaxima +" pontos", larguraDispositivo / 2 - 140, alturaDispositivo / 2 - gameOver.getHeight());
        }

        batch.end();
    }

    public void validarPontos(){


        if (posicaoCanoHorizontal < 50 - passaros[0].getWidth()) {
            // Passou da posição do pássaro
            if (!passouCano) {
                pontos++;
                passouCano = true;
                somPontuacao.play();
            }
        }

        variacao += Gdx.graphics.getDeltaTime() * 10;
        // Verifica variação para bater asas do pássaro
        if (variacao > 3)
            variacao = 0;
    }


    private  void inicializarTexturas(){
        passaros = new Texture[3];
        passaros[0] = new Texture("passaro1.png");
        passaros[1] = new Texture("passaro2.png");
        passaros[2] = new Texture("passaro3.png");

        fundo = new Texture("fundo.png");
        canoBaixo = new Texture("cano_baixo_maior.png");
        canoTopo = new Texture("cano_topo_maior.png");
        gameOver = new Texture("game_over.png");

    }

    private void inicializarObjetos(){

        batch = new SpriteBatch();
        random = new Random();

        larguraDispositivo = VIRTUAL_WIDTH;
        alturaDispositivo = VIRTUAL_HEIGHT;
        posicaoPassaroY = alturaDispositivo / 2;
        posicaoCanoHorizontal = larguraDispositivo;
        espacoEntreCanos = 250;

        // Configuração do texto
        textoPontuacao = new BitmapFont();
        textoPontuacao.setColor(Color.WHITE);
        textoPontuacao.getData().setScale(6);

        textoReiniciair = new BitmapFont();
        textoReiniciair.setColor(Color.GREEN);
        textoReiniciair.getData().setScale(2);

        textoMelhorPontuacao = new BitmapFont();
        textoMelhorPontuacao.setColor(Color.RED);
        textoMelhorPontuacao.getData().setScale(2);

        // Formas geométricas para colisões
        shapeRenderer = new ShapeRenderer();
        circuloPassaro = new Circle();
        retanguloCanoBaixo = new Rectangle();
        retanguloCanoTopo = new Rectangle();

        // Inicializa sons
        somVoando = Gdx.audio.newSound(Gdx.files.internal("som_asa.wav"));
        somColisao = Gdx.audio.newSound(Gdx.files.internal("som_colisao.wav"));
        somPontuacao = Gdx.audio.newSound(Gdx.files.internal("som_pontos.wav"));

        // Configurações de preferencias dos objetos
        preferencias = Gdx.app.getPreferences("FlappyBird");
        pontuacaoMaxima = preferencias.getInteger("pontuacaoMaxima", 0);

        // Configurações da câmera
        camera = new OrthographicCamera();
        camera.position.set(VIRTUAL_WIDTH / 2, VIRTUAL_HEIGHT / 2, 0);
        viewport = new StretchViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);

    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void dispose() {
        Gdx.app.log("dispose", "Descarte de conteúdos");
    }
}
