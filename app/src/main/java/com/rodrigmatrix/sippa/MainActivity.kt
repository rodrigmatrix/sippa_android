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
                "<!--########################################################################################################-->\n" +
                "\t\t\t</div><!-- fim da esquerda -->\n" +
                "\t\t\t<div id=\"direita\" class=\"grid_12 omega\">\n" +
                "            <!-- SOMENTE ESSA PARTE PODE SER EDITADA! -->\n" +
                "\n" +
                "                \n" +
                "                <h2>Arquivos</h2>\n" +
                "                \n" +
                "\n" +
                "\n" +
                "                \n" +
                "                \n" +
                "\n" +
                "                <br>\n" +
                "                    \n" +
                "                   \n" +
                "\n" +
                "                    Organizar arquivos:\n" +
                "                    <a href=\"../sippa/aluno_visualizar_arquivos.jsp?sorter=1\"><b>por data</b></a> &nbsp;&nbsp;\n" +
                "                    <a href=\"../sippa/aluno_visualizar_arquivos.jsp?sorter=0\"><b>por nome</b></a>\n" +
                "                    <table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" class=\"tabela_ver_freq\">\n" +
                "\t<thead>\n" +
                "\t\t<tr>\n" +
                "\t\t\t<th>Arquivo</th>\n" +
                "\t\t</tr>\n" +
                "\t</thead>\n" +
                "\t<tbody>\n" +
                "\t\t<tr>\n" +
                "\t\t\t<td class=\"tab-esquerda\"><a href=\"../ServletCentral?comando=CmdLoadArquivo&id=Seminarios.pdf\"/>Seminarios.pdf</a>  -  3/5/2019</td>\n" +
                "\t\t</tr>\n" +
                "\t\t<tr>\n" +
                "\t\t\t<td class=\"tab-esquerda\"><a href=\"../ServletCentral?comando=CmdLoadArquivo&id=Aulas_13_e_14_Qualidade_2019.1_-_Scrum.pdf\"/>Aulas_13_e_14_Qualidade_2019.1_-_Scrum.pdf</a>  -  26/4/2019</td>\n" +
                "\t\t</tr>\n" +
                "\t\t<tr>\n" +
                "\t\t\t<td class=\"tab-esquerda\"><a href=\"../ServletCentral?comando=CmdLoadArquivo&id=Qualidade-ListaExercicios1.pdf\"/>Qualidade-ListaExercicios1.pdf</a>  -  26/4/2019</td>\n" +
                "\t\t</tr>\n" +
                "\t\t<tr>\n" +
                "\t\t\t<td class=\"tab-esquerda\"><a href=\"../ServletCentral?comando=CmdLoadArquivo&id=Aula_10_e_11-_QUAL_2019.1_-_Medicao_e_Analise.pdf\"/>Aula_10_e_11-_QUAL_2019.1_-_Medicao_e_Analise.pdf</a>  -  8/4/2019</td>\n" +
                "\t\t</tr>\n" +
                "\t\t<tr>\n" +
                "\t\t\t<td class=\"tab-esquerda\"><a href=\"../ServletCentral?comando=CmdLoadArquivo&id=Aula_7_8_e_9-_QUAL_2019.1_-_Normas_ISO.pdf\"/>Aula_7_8_e_9-_QUAL_2019.1_-_Normas_ISO.pdf</a>  -  8/4/2019</td>\n" +
                "\t\t</tr>\n" +
                "\t\t<tr>\n" +
                "\t\t\t<td class=\"tab-esquerda\"><a href=\"../ServletCentral?comando=CmdLoadArquivo&id=Aula_6-_PROCESSO_2019.1_-_CMMI.pdf\"/>Aula_6-_PROCESSO_2019.1_-_CMMI.pdf</a>  -  8/4/2019</td>\n" +
                "\t\t</tr>\n" +
                "\t\t<tr>\n" +
                "\t\t\t<td class=\"tab-esquerda\"><a href=\"../ServletCentral?comando=CmdLoadArquivo&id=Aula_4-_QUAL_2019.1_-_Qualidade_do_Processo_e_do_Produto.pdf\"/>Aula_4-_QUAL_2019.1_-_Qualidade_do_Processo_e_do_Produto.pdf</a>  -  14/3/2019</td>\n" +
                "\t\t</tr>\n" +
                "\t\t<tr>\n" +
                "\t\t\t<td class=\"tab-esquerda\"><a href=\"../ServletCentral?comando=CmdLoadArquivo&id=Aula_5-_PROCESSO_2019.1_-_MPSBR.pdf\"/>Aula_5-_PROCESSO_2019.1_-_MPSBR.pdf</a>  -  14/3/2019</td>\n" +
                "\t\t</tr>\n" +
                "\t\t<tr>\n" +
                "\t\t\t<td class=\"tab-esquerda\"><a href=\"../ServletCentral?comando=CmdLoadArquivo&id=Aula_3-_QUAL_2019.1_-_Fatores_Humanos_de_Qualidade.pdf\"/>Aula_3-_QUAL_2019.1_-_Fatores_Humanos_de_Qualidade.pdf</a>  -  2/3/2019</td>\n" +
                "\t\t</tr>\n" +
                "\t\t<tr>\n" +
                "\t\t\t<td class=\"tab-esquerda\"><a href=\"../ServletCentral?comando=CmdLoadArquivo&id=Aula_2-_QUAL_2019.1_-_Introducao_a_Qualidade_de_Software.pdf\"/>Aula_2-_QUAL_2019.1_-_Introducao_a_Qualidade_de_Software.pdf</a>  -  2/3/2019</td>\n" +
                "\t\t</tr>\n" +
                "\t\t<tr>\n" +
                "\t\t\t<td class=\"tab-esquerda\"><a href=\"../ServletCentral?comando=CmdLoadArquivo&id=Aula_1-_QUAL_2019.1_-_Apresentacao_da_disciplina.pdf\"/>Aula_1-_QUAL_2019.1_-_Apresentacao_da_disciplina.pdf</a>  -  2/3/2019</td>\n" +
                "\t\t</tr>\n" +
                "\t</tbody>\n" +
                "</table>\n" +
                "\n" +
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
        //serializer.parseHorasComplementares(res, database)
        //serializer.parseClasses(res)
        //serializer.parseGrades(res, database)
        serializer.parseFiles(res, database)
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
