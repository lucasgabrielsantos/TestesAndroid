package br.com.alura.leilao.ui.activity;

import android.content.Intent;
import android.support.test.espresso.matcher.RootMatchers;
import android.support.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;

import br.com.alura.leilao.BaseTesteIntegracao;
import br.com.alura.leilao.R;
import br.com.alura.leilao.formatter.FormatadorDeMoeda;
import br.com.alura.leilao.model.Leilao;
import br.com.alura.leilao.model.Usuario;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.matcher.RootMatchers.isPlatformPopup;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

public class LancesLeilaoTelaTest extends BaseTesteIntegracao {

    @Rule
    public ActivityTestRule<ListaLeilaoActivity> mainActivity =
            new ActivityTestRule<>(ListaLeilaoActivity.class, true, false);

    @Before
    public void setup() throws IOException {
        limpaBancoDeDadosDaApi();
        limpaBancoDeDadosInterno();
    }

    @Test
    public void deve_AtualizarLancesDosLeilao_QuandoReceberUmLance() throws IOException {
//      Salvar leilao na api
        tentaSalvarLeilaoNaApi(new Leilao("Carro"));

//      Inicializar a main Activity
        mainActivity.launchActivity(new Intent());

//      Clica no leilão
        onView(withId(R.id.lista_leilao_recyclerview))
                .perform(actionOnItemAtPosition(0, click()));

//      Clica no fab da tela de lances do leilão
        onView(withId(R.id.lances_leilao_fab_adiciona))
                .perform(click());

//      Verifica se aparece dialog de aviso de nao ter usuario com titulo e mensagem esperada
        onView(withText("Usuários não encontrados"))
                .check(matches(isDisplayed()));

        onView(withText("Não existe usuários cadastrados! Cadastre um usuário para propor o lance."))
                .check(matches(isDisplayed()));

//      Clica no botão cadastrar usuário
        onView(withText("Cadastrar usuário"))
                .perform(click());

//      Clica no fab da tela de lista de usuarios
        onView(allOf(withId(R.id.lista_usuario_fab_adiciona),
                isDisplayed()))
                .perform(click());

//      Clica no Edittext e preenche com o nome do usuário
        onView(allOf(withId(R.id.form_usuario_nome),
                isDisplayed()))
                .perform(click());

        onView(allOf(withId(R.id.form_usuario_nome_edittext),
                isDisplayed()))
                .perform(replaceText("Lucas"), closeSoftKeyboard());

//      Clica em adicionar
        onView(allOf(withId(android.R.id.button1),
                withText("Adicionar"),
                isDisplayed()))
                .perform(scrollTo(), click());

//      Clica em Onback do Android()
        pressBack();

//      Clica no fab lances do leilão
        onView(withId(R.id.lances_leilao_fab_adiciona))
                .perform(click());

//      Verifica visibilidade do dialog com o titulo esperado("Novo Lance")
        onView(withText("Novo lance"))
                .check(matches(isDisplayed()));

//      Clica no edittext do valor e preenche
        onView(withId(R.id.form_lance_valor_edittext))
                .perform(click(),
                        replaceText("200"),
                        closeSoftKeyboard());

//      Seleciona usuario
        onView(withId(R.id.form_lance_usuario)).perform(click());
        onData(is(new Usuario(1, "Lucas")))
                .inRoot(isPlatformPopup())
                .perform(click());

//      Clica no botão propor
        onView(withText("Propor")).perform(click());

//      Fazer assertion para as views de maior e menor lance e também para os maiores lances
        FormatadorDeMoeda formatador = new FormatadorDeMoeda();
        onView(withId(R.id.lances_leilao_maior_lance))
                .check(matches(allOf(withText(formatador.formata(200)), isDisplayed())));

        onView(withId(R.id.lances_leilao_menor_lance))
                .check((matches(allOf(withText(formatador.formata(200)), isDisplayed()))));

        onView(withId(R.id.lances_leilao_maiores_lances))
                .check(matches(allOf(withText("200.0 - (1) Lucas\n"), isDisplayed())));
    }

    @After
    public void teardown() throws IOException {
        limpaBancoDeDadosDaApi();
        limpaBancoDeDadosInterno();
    }
}
