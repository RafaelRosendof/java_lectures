public class Main {
    public static void main(String[] args) {
        // Instanciando um objeto do tipo Professor
        Professor professor = new Professor();

        // Chamando os métodos da interface Professor
        professor.Tenho_acesso();
        professor.Aplicar_prova();
        professor.Suspender_aluno();

        // Testando a herança de Usuario
        professor.Tem_matricula();

        // Instanciando um objeto do tipo Alunos
        Alunos aluno = new Alunos();

        // Chamando os métodos da interface Aluno
//        aluno.Tenho_acesso();
        aluno.Fazendo_prova("Matemática");
        aluno.listarMaterias();

        // Testando a herança de Usuario
//        aluno.Tem_matricula();
    }
}

