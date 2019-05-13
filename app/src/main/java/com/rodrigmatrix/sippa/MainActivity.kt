package com.rodrigmatrix.sippa

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.core.text.parseAsHtml
import androidx.core.view.isVisible
import androidx.room.Room
import com.rodrigmatrix.sippa.Serializer.Serializer
import com.rodrigmatrix.sippa.persistance.StudentsDatabase
import org.jetbrains.anko.doAsync

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val loginbtn = findViewById<View>(R.id.login) as Button
        val captcha_image = findViewById<View>(R.id.captcha_image) as ImageView
        //val progress = findViewById<View>(R.id.progressLogin) as ProgressBar
        //progress.isVisible = false
        var res = "<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\n" +
                "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" +
                "<head>\n" +
                "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=iso-8859-1\" />\n" +
                "<title>SIPPA | Sistema de Presenças e Planos de Aula</title>\n" +
                "<link rel=\"shortcut icon\" href=\"../images/favicon.ico\" type=\"image/x-icon\" />\n" +
                "\n" +
                "<script type=\"text/javascript\" src=\"js/jquery.js\"></script>\n" +
                "<script type=\"text/javascript\" src=\"js/jquery.dimensions.js\"></script>\n" +
                "<script type=\"text/javascript\" src=\"js/jquery.positionBy.js\"></script>\n" +
                "<script type=\"text/javascript\" src=\"js/jquery.bgiframe.js\"></script>\n" +
                "<script type=\"text/javascript\" src=\"js/jquery.jdMenu.js\"></script>\n" +
                "</head>\n" +
                "\n" +
                "<body>\n" +
                "    <!-- Css alterado de posição -->\n" +
                "    <link href=\"css/wire.css\" rel=\"stylesheet\" type=\"text/css\" />\n" +
                "    <!-- ! -->\n" +
                "\t<div id=\"wrapper\" class=\"container_16\">\n" +
                "\t\t<div id=\"topo\" class=\"grid_16\">\n" +
                "\t\t\t\t<div id=\"logo\" class=\"grid_4 alpha\">\n" +
                "\t\t\t\t\t<img src=\"images/sippa_logo.png\" />\n" +
                "\t\t\t\t</div><!-- fim do logo -->\n" +
                "\t\t\t\t<div id=\"opc_usuario\" class=\"grid_11 alpha omega\">\n" +
                "\t\t\t\t\t<h1>Olá ALUNO(A) RODRIGO GOMES RESENDE </h1>\n" +
                "\t\t\t\t\t<br/>\n" +
                "\t\t\t\t\t<ul>\n" +
                "                                            <li><a href=\"../ServletCentral?comando=CmdListarDisciplinaAluno\">Disciplinas</a></li>\n" +
                "                                            <li><a href=\"../ServletCentral?comando=CmdVisualizarIntegralizacaoCurricularAluno\">Integralização Curricular</a></li>\n" +
                "                                            <li><a href=\"../ServletCentral?comando=CmdVisualizarAlunoDados\">Dados Cadastrais</a></li>\n" +
                "                                            <li><a href=\"../ServletCentral?comando=CmdListarReclamacoesAluno&page=1&max=15\">Reclamações</a></li>\n" +
                "                                            <li><a href=\"../ServletCentral?comando=CmdLoginSaviAluno\">SAVI</a></li>\n" +
                "                                            <li><a href=\"../ServletCentral?comando=CmdLoginSisacAluno\">SISAC</a></li>\n" +
                "\t\t\t\t\t\t<li><a href=\"../\">Sistemas</a></li>\n" +
                "\t\t\t\t\t\t<li><a href=\"../ServletCentral?comando=CmdLogout\">Sair</a></li>\n" +
                "\t\t\t\t\t</ul>\n" +
                "                \n" +
                "                \n" +
                "\n" +
                "                                </div><!-- fim do opc_usuario -->\n" +
                "\n" +
                "\t\t</div><!-- fim do topo -->\n" +
                "\n" +
                "                \n" +
                "                    <div class=\"grid_16 alpha omega\" id=\"info\">\n" +
                "    <h1>QXD0042 - Qualidade de Software - 01A</h1>\n" +
                "    <h2>Prof(a). Carla Ilane Moreira Bezerra - carlailane@gmail.com</h2>\n" +
                "</div>\n" +
                "                \n" +
                "\n" +
                "\n" +
                "\t\t<div id=\"corpo\" class=\"grid_16\">\n" +
                "\t\t\t<div id=\"esquerda\" class=\"grid_4 alpha\">\n" +
                "                                    \n" +
                "    <!-- Imprimindo o menu -->\n" +
                "    <ul id=\"nav\" class=\"grid_4 jd_menu jd_menu_vertical\">\t\t<li class=\"item_menu\" ><a href=\"../sippa/aluno_conferir_freq_resultado.jsp\">Notícias</a></li>\n" +
                "\t\t<li class=\"item_menu\" ><a href=\"../sippa/aluno_visualizar_arquivos.jsp?sorter=1\">Arquivos</a></li>\n" +
                "\t\t<li class=\"item_menu\" ><a href=\"../ServletCentral?comando=CmdVisualizarAvaliacoesAluno\">Avaliações</a></li>\n" +
                "\t\t<li class=\"item_menu\" ><a href=\"../sippa/aluno_enviar_trabalhos.jsp\">Enviar Trabalhos</a></li>\n" +
                "\t\t<li class=\"item_menu\" ><a href=\"../sippa/aluno_cadastrar_solicitacao.jsp\">Solicitar 2a Chamada</a></li>\n" +
                "\t\t<li class=\"item_menu\" ><a href=\"../sippa/aluno_listar_recessos.jsp\">Calendário</a></li>\n" +
                "\t\t<li class=\"item_menu\" ><a href=\"../ServletCentral?comando=CmdGerarPlanoAula\">Gerar Plano</a></li>\n" +
                "\t\t<li class=\"item_menu\" ><a href=\"../ServletCentral?comando=CmdGerarDiario\">Gerar Diário</a></li>\n" +
                "</ul>\n" +
                "<!--####################    Código do messenger. NÃO MODIFIQUE! ##############################################-->\n" +
                "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\n" +
                "<html>\n" +
                "  <head>\n" +
                "    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n" +
                "    <link type=\"text/css\" rel=\"stylesheet\" href=\"../messenger/messenger.css\">\n" +
                "    <script type=\"text/javascript\" language=\"javascript\" src=\"../messenger/messenger.nocache.js\"></script>    \n" +
                "  </head>\n" +
                "  <body>\n" +
                "    <table>\n" +
                "       <tr><td valign=\"top\">\n" +
                "            <div id=\"messengerView\"></div>\n" +
                "       </td></tr>\n" +
                "    </table>\n" +
                "  </body>\n" +
                "</html>\n" +
                "\n" +
                "\n" +
                "<!--########################################################################################################-->              \n" +
                "\t\t\t</div><!-- fim da esquerda -->\n" +
                "\t\t\t<div id=\"direita\" class=\"grid_12 omega\">\n" +
                "            <!-- SOMENTE ESSA PARTE PODE SER EDITADA! -->\n" +
                "                <h2>Últimas Notícias</h2>\n" +
                "                <div class=\"tabContainer\" id=\"lista\">\n" +
                "                    <div class=\"scrollContainer\">\n" +
                "                        <table>\n" +
                "                        \n" +
                "                            <tr>\n" +
                "                                <td class=\"tabela-coluna0\">\n" +
                "                                    03/05/2019\n" +
                "                                <td class=\"tabela-coluna1\">O Trabalho de Seminários está postado nos arquivos do SIPPA. Verifiquem seu artigo e o dia da apresentação alocado.</td>\n" +
                "                            </tr>\n" +
                "                        \n" +
                "                            <tr>\n" +
                "                                <td class=\"tabela-coluna0\">\n" +
                "                                    26/04/2019\n" +
                "                                <td class=\"tabela-coluna1\">Pessoal, a aula de scrum e a lista de exercícios já estão na lista de arquivos do SIPPA.</td>\n" +
                "                            </tr>\n" +
                "                        \n" +
                "                            <tr>\n" +
                "                                <td class=\"tabela-coluna0\">\n" +
                "                                    01/04/2019\n" +
                "                                <td class=\"tabela-coluna1\">Pessoal, tou muito doente. Não vou conseguir ir para aula hoje. </td>\n" +
                "                            </tr>\n" +
                "                        \n" +
                "                        </table>\n" +
                "                    </div>\n" +
                "                </div>\n" +
                "                <div>\n" +
                "                    <br>\n" +
                "<!--                <h1>Qualidade de Software</h1> -->\n" +
                "                <h3>80% de Frequência; 24 Presenças em Horas;  6 Faltas em Horas </h3>\n" +
                "\n" +
                "                <table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" class=\"tabela_ver_freq\">\n" +
                "\t<thead>\n" +
                "\t\t<tr>\n" +
                "\t\t\t<th>Aula</th>\n" +
                "\t\t\t<th>Plano de Aula </th>\n" +
                "\t\t\t<th>Diário de Aula </th>\n" +
                "\t\t\t<th>Presença</th>\n" +
                "\t\t</tr>\n" +
                "\t</thead>\n" +
                "\t<tbody>\n" +
                "\t\t<tr>\n" +
                "\t\t\t<td>1</td>\n" +
                "\t\t\t<td>18/02/2019<br>\n" +
                "\t\t\t- Apresentação da disciplina: Conteúdo, ementa, avaliação e motivação</td>\n" +
                "\t\t\t<td>18/02/2019<br>\n" +
                "\t\t\t- Apresentação da disciplina: Conteúdo, ementa, avaliação e motivação (25/02)</td>\n" +
                "\t\t\t<td>2</td>\n" +
                "\t\t</tr>\n" +
                "\t\t<tr>\n" +
                "\t\t\t<td>2</td>\n" +
                "\t\t\t<td>19/02/2019<br>\n" +
                "\t\t\t- Introdução a Qualidade de Software</td>\n" +
                "\t\t\t<td>19/02/2019<br>\n" +
                "\t\t\t- Introdução a Qualidade de Software (26/02)</td>\n" +
                "\t\t\t<td>2</td>\n" +
                "\t\t</tr>\n" +
                "\t\t<tr>\n" +
                "\t\t\t<td>3</td>\n" +
                "\t\t\t<td>25/02/2019<br>\n" +
                "\t\t\t- Fatores Humanos de Qualidade</td>\n" +
                "\t\t\t<td>25/02/2019<br>\n" +
                "\t\t\t- Fatores Humanos de Qualidade (26/02) - Aula extra</td>\n" +
                "\t\t\t<td>2</td>\n" +
                "\t\t</tr>\n" +
                "\t\t<tr>\n" +
                "\t\t\t<td>4</td>\n" +
                "\t\t\t<td>26/02/2019<br>\n" +
                "\t\t\t- Qualidade de Processo e Qualidade do Produto</td>\n" +
                "\t\t\t<td>26/02/2019<br>\n" +
                "\t\t\t- Qualidade de Processo e Qualidade do Produto</td>\n" +
                "\t\t\t<td>2</td>\n" +
                "\t\t</tr>\n" +
                "\t\t<tr>\n" +
                "\t\t\t<td>5</td>\n" +
                "\t\t\t<td>11/03/2019<br>\n" +
                "\t\t\t- Normas ISO: ISO 9001-2008</td>\n" +
                "\t\t\t<td>11/03/2019<br>\n" +
                "\t\t\t- MPSBR</td>\n" +
                "\t\t\t<td>2</td>\n" +
                "\t\t</tr>\n" +
                "\t\t<tr>\n" +
                "\t\t\t<td>6</td>\n" +
                "\t\t\t<td>12/03/2019<br>\n" +
                "\t\t\t- Normas ISO: ISO 12207 e ISO 15504</td>\n" +
                "\t\t\t<td>12/03/2019<br>\n" +
                "\t\t\t- MPSBR - Exercício</td>\n" +
                "\t\t\t<td>2</td>\n" +
                "\t\t</tr>\n" +
                "\t\t<tr>\n" +
                "\t\t\t<td>7</td>\n" +
                "\t\t\t<td>18/03/2019<br>\n" +
                "\t\t\t- Normas ISO: ISO 9126 e ISO 25000</td>\n" +
                "\t\t\t<td>18/03/2019<br>\n" +
                "\t\t\t- CMMI</td>\n" +
                "\t\t\t<td>2</td>\n" +
                "\t\t</tr>\n" +
                "\t\t<tr>\n" +
                "\t\t\t<td>8</td>\n" +
                "\t\t\t<td>26/03/2019<br>\n" +
                "\t\t\t- Medição e Análise</td>\n" +
                "\t\t\t<td>26/03/2019<br>\n" +
                "\t\t\t- Normas ISO</td>\n" +
                "\t\t\t<td>0</td>\n" +
                "\t\t</tr>\n" +
                "\t\t<tr>\n" +
                "\t\t\t<td>9</td>\n" +
                "\t\t\t<td>01/04/2019<br>\n" +
                "\t\t\t- Medição e Análise - GQM</td>\n" +
                "\t\t\t<td>01/04/2019<br>\n" +
                "\t\t\t- ISO 25000 e 9126</td>\n" +
                "\t\t\t<td>2</td>\n" +
                "\t\t</tr>\n" +
                "\t\t<tr>\n" +
                "\t\t\t<td>10</td>\n" +
                "\t\t\t<td>02/04/2019<br>\n" +
                "\t\t\t- Modelos de Processo: Introdução ao MPSBR</td>\n" +
                "\t\t\t<td>02/04/2019<br>\n" +
                "\t\t\t- Medição e Análise</td>\n" +
                "\t\t\t<td>0</td>\n" +
                "\t\t</tr>\n" +
                "\t\t<tr>\n" +
                "\t\t\t<td>11</td>\n" +
                "\t\t\t<td>08/04/2019<br>\n" +
                "\t\t\t- Modelos de Processo: MPSBR Níveis G e F</td>\n" +
                "\t\t\t<td>08/04/2019<br>\n" +
                "\t\t\t- Medição e Análise</td>\n" +
                "\t\t\t<td>2</td>\n" +
                "\t\t</tr>\n" +
                "\t\t<tr>\n" +
                "\t\t\t<td>12</td>\n" +
                "\t\t\t<td>09/04/2019<br>\n" +
                "\t\t\t- Modelos de Processo: MPSBR Níveis E, D e C- Modelos de Processo: MPSBR Níveis A e B</td>\n" +
                "\t\t\t<td>09/04/2019<br>\n" +
                "\t\t\t- Medição e Análise</td>\n" +
                "\t\t\t<td>0</td>\n" +
                "\t\t</tr>\n" +
                "\t\t<tr>\n" +
                "\t\t\t<td>13</td>\n" +
                "\t\t\t<td>15/04/2019<br>\n" +
                "\t\t\t- Modelos de Processo: Introdução ao CMMI</td>\n" +
                "\t\t\t<td>15/04/2019<br>\n" +
                "\t\t\t- Scrum</td>\n" +
                "\t\t\t<td>2</td>\n" +
                "\t\t</tr>\n" +
                "\t\t<tr>\n" +
                "\t\t\t<td>14</td>\n" +
                "\t\t\t<td>16/04/2019<br>\n" +
                "\t\t\t- Modelos de Processo: CMMI Níveis 2, 3, 4 e 5</td>\n" +
                "\t\t\t<td>16/04/2019<br>\n" +
                "\t\t\t- Scrum</td>\n" +
                "\t\t\t<td>2</td>\n" +
                "\t\t</tr>\n" +
                "\t\t<tr>\n" +
                "\t\t\t<td>15</td>\n" +
                "\t\t\t<td>22/04/2019<br>\n" +
                "\t\t\t- Processos Ágeis: Scrum e XP</td>\n" +
                "\t\t\t<td>22/04/2019<br>\n" +
                "\t\t\t- Aula de revisão da prova</td>\n" +
                "\t\t\t<td>2</td>\n" +
                "\t\t</tr>\n" +
                "\t\t<tr>\n" +
                "\t\t\t<td>16</td>\n" +
                "\t\t\t<td>23/04/2019<br>\n" +
                "\t\t\t- Verificação e Validação\n" +
                "- Apresentação do Trabalho Seminários dos Artigos (AP2)</td>\n" +
                "\t\t\t<td>23/04/2019<br>\n" +
                "\t\t\t- Apresentação dos trabalhos</td>\n" +
                "\t\t\t<td></td>\n" +
                "\t\t</tr>\n" +
                "\t\t<tr>\n" +
                "\t\t\t<td>17</td>\n" +
                "\t\t\t<td>29/04/2019<br>\n" +
                "\t\t\t- Revisão para Avaliação Parcial 1</td>\n" +
                "\t\t\t<td><br>\n" +
                "\t\t\t</td>\n" +
                "\t\t\t<td></td>\n" +
                "\t\t</tr>\n" +
                "\t\t<tr>\n" +
                "\t\t\t<td>18</td>\n" +
                "\t\t\t<td>30/04/2019<br>\n" +
                "\t\t\t- Primeira Avaliação Parcial (AP1)</td>\n" +
                "\t\t\t<td><br>\n" +
                "\t\t\t</td>\n" +
                "\t\t\t<td></td>\n" +
                "\t\t</tr>\n" +
                "\t\t<tr>\n" +
                "\t\t\t<td>19</td>\n" +
                "\t\t\t<td>06/05/2019<br>\n" +
                "\t\t\t- Correção da Prova\n" +
                "-  Apresentação do Trabalho Final (AP3)</td>\n" +
                "\t\t\t<td><br>\n" +
                "\t\t\t</td>\n" +
                "\t\t\t<td></td>\n" +
                "\t\t</tr>\n" +
                "\t\t<tr>\n" +
                "\t\t\t<td>20</td>\n" +
                "\t\t\t<td>07/05/2019<br>\n" +
                "\t\t\t- Modelagem de Processos</td>\n" +
                "\t\t\t<td><br>\n" +
                "\t\t\t</td>\n" +
                "\t\t\t<td></td>\n" +
                "\t\t</tr>\n" +
                "\t\t<tr>\n" +
                "\t\t\t<td>21</td>\n" +
                "\t\t\t<td>13/05/2019<br>\n" +
                "\t\t\t- Ferramenta BPMN</td>\n" +
                "\t\t\t<td><br>\n" +
                "\t\t\t</td>\n" +
                "\t\t\t<td></td>\n" +
                "\t\t</tr>\n" +
                "\t\t<tr>\n" +
                "\t\t\t<td>22</td>\n" +
                "\t\t\t<td>14/05/2019<br>\n" +
                "\t\t\t- Execução do Trabalho Prático (AP3)</td>\n" +
                "\t\t\t<td><br>\n" +
                "\t\t\t</td>\n" +
                "\t\t\t<td></td>\n" +
                "\t\t</tr>\n" +
                "\t\t<tr>\n" +
                "\t\t\t<td>23</td>\n" +
                "\t\t\t<td>20/05/2019<br>\n" +
                "\t\t\t- Seminários dos artigos (AP2)</td>\n" +
                "\t\t\t<td><br>\n" +
                "\t\t\t</td>\n" +
                "\t\t\t<td></td>\n" +
                "\t\t</tr>\n" +
                "\t\t<tr>\n" +
                "\t\t\t<td>24</td>\n" +
                "\t\t\t<td>21/05/2019<br>\n" +
                "\t\t\t- Seminários dos artigos (AP2)</td>\n" +
                "\t\t\t<td><br>\n" +
                "\t\t\t</td>\n" +
                "\t\t\t<td></td>\n" +
                "\t\t</tr>\n" +
                "\t\t<tr>\n" +
                "\t\t\t<td>25</td>\n" +
                "\t\t\t<td>27/05/2019<br>\n" +
                "\t\t\t- Seminários dos artigos (AP2)</td>\n" +
                "\t\t\t<td><br>\n" +
                "\t\t\t</td>\n" +
                "\t\t\t<td></td>\n" +
                "\t\t</tr>\n" +
                "\t\t<tr>\n" +
                "\t\t\t<td>26</td>\n" +
                "\t\t\t<td>28/05/2019<br>\n" +
                "\t\t\t- Seminários dos artigos (AP2)</td>\n" +
                "\t\t\t<td><br>\n" +
                "\t\t\t</td>\n" +
                "\t\t\t<td></td>\n" +
                "\t\t</tr>\n" +
                "\t\t<tr>\n" +
                "\t\t\t<td>27</td>\n" +
                "\t\t\t<td>03/06/2019<br>\n" +
                "\t\t\t- Seminários dos artigos (AP2)</td>\n" +
                "\t\t\t<td><br>\n" +
                "\t\t\t</td>\n" +
                "\t\t\t<td></td>\n" +
                "\t\t</tr>\n" +
                "\t\t<tr>\n" +
                "\t\t\t<td>28</td>\n" +
                "\t\t\t<td>04/06/2019<br>\n" +
                "\t\t\t- Seminários dos artigos (AP2)</td>\n" +
                "\t\t\t<td><br>\n" +
                "\t\t\t</td>\n" +
                "\t\t\t<td></td>\n" +
                "\t\t</tr>\n" +
                "\t\t<tr>\n" +
                "\t\t\t<td>29</td>\n" +
                "\t\t\t<td>10/06/2019<br>\n" +
                "\t\t\t- Seminários dos artigos (AP2)</td>\n" +
                "\t\t\t<td><br>\n" +
                "\t\t\t</td>\n" +
                "\t\t\t<td></td>\n" +
                "\t\t</tr>\n" +
                "\t\t<tr>\n" +
                "\t\t\t<td>30</td>\n" +
                "\t\t\t<td>11/06/2019<br>\n" +
                "\t\t\t- Entrega e Apresentação de Trabalho (AP3)</td>\n" +
                "\t\t\t<td><br>\n" +
                "\t\t\t</td>\n" +
                "\t\t\t<td></td>\n" +
                "\t\t</tr>\n" +
                "\t\t<tr>\n" +
                "\t\t\t<td>31</td>\n" +
                "\t\t\t<td>17/06/2019<br>\n" +
                "\t\t\t- Entrega e Apresentação de Trabalho (AP3)</td>\n" +
                "\t\t\t<td><br>\n" +
                "\t\t\t</td>\n" +
                "\t\t\t<td></td>\n" +
                "\t\t</tr>\n" +
                "\t\t<tr>\n" +
                "\t\t\t<td>32</td>\n" +
                "\t\t\t<td>18/06/2019<br>\n" +
                "\t\t\t- Entrega de Resultados</td>\n" +
                "\t\t\t<td><br>\n" +
                "\t\t\t</td>\n" +
                "\t\t\t<td></td>\n" +
                "\t\t</tr>\n" +
                "\t</tbody>\n" +
                "</table>\n" +
                "\n" +
                "                </div>\n" +
                "\n" +
                "            <!-- FIM DA PARTE PODE SER EDITADA! -->\n" +
                "\t\t\t</div><!-- fim da direita -->\n" +
                "\t\t</div><!-- fim do corpo -->\n" +
                "\t\t<div id=\"rodape\" class=\"grid_16\">\n" +
                "\t\t\tUniversidade Federal do Ceará\n" +
                "\t\t</div><!-- fim do rodape -->\n" +
                "\t</div> <!-- fim do wrapper -->\n" +
                "</body>\n" +
                "</html>"
        val serializer = Serializer()
        val api = Api()
        val database = Room.databaseBuilder(
            applicationContext,
            StudentsDatabase::class.java, "database.db")
            .fallbackToDestructiveMigration()
            .build()
        //serializer.parseHorasComplementares(res)
        //serializer.parseClasses(res)
        //serializer.parseGrades(res)
        serializer.parseAttendance(res)
        api.getCaptcha(database, captcha_image)
        var login = findViewById<EditText>(R.id.login_input)
        var password = findViewById<EditText>(R.id.password_input)
        var captcha_input = findViewById<EditText>(R.id.captcha_input)
        var view = findViewById<View>(R.id.main_activity)
        loginbtn.setOnClickListener{
            //progress.isVisible = true
            val thread = Thread {
                var jsession = database.StudentDao().getStudent().jsession
                api.login(login.text.toString(), password.text.toString(), captcha_input.text.toString(), jsession, this@MainActivity, view, captcha_image, loginbtn, database)
            }
            thread.start()
        }
    }
}
