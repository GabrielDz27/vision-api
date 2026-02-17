public class ProcessaImagemController {

    @Autowired
    private VisionService visionService;

    @Autowired
    private ImagemRepository imagemRepository; // Seu repositório JPA

    public void processarUpload(File arquivo) {
        // 1. Pergunta para o Python o que tem na imagem
        // 1. Pede o Hash para o Python
        VisionResponseDTO resultadoIA = visionService.analisarImagem(novaFoto);

        if (resultadoIA != null) {
            // 2. Busca no banco se existe alguma imagem com esse mesmo Hash
            Optional<Imagem> imagemExistente = imagemRepository.findByHashIdentificador(resultadoIA.imagem_id);

            if (imagemExistente.isPresent()) {
                // Se cair aqui, a imagem é repetida!
                Imagem imgNoBanco = imagemExistente.get();
                Objeto objetoRelacionado = imgNoBanco.getObjeto();

                System.out.println("--- ITEM JÁ EXISTENTE ---");
                System.out.println("Título: " + objetoRelacionado.getTitulo());
                System.out.println("Data em que você comprou: " + imgNoBanco.getDataUpload());
                System.out.println("Link da foto antiga: " + imgNoBanco.getUrlNuvem());
                
                // Aqui você envia para o seu Front-end (Thymeleaf, React, etc) 
                // tanto a 'novaFoto' quanto a 'imgNoBanco.getUrlNuvem()'
            } else {
                System.out.println("NOVO ITEM: Identificado como " + resultadoIA.objetos.get(0).classe);
                // Lógica para salvar as 5 entidades no banco...
            }
        }
    }
}