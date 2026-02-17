# 'import' é como o 'import' do Java. 
# 'from x import y' é para pegar apenas uma peça específica de uma biblioteca.
from fastapi import FastAPI, File, UploadFile
from ultralytics import YOLO
from PIL import Image
import imagehash
import io

# Instanciamos o objeto principal da API.
app = FastAPI()

# Carregamos o modelo. O Python gerencia a memória de forma inteligente aqui.
modelo = YOLO("yolov8n.pt")

# @app.post é um "Decorator". Funciona como as Annotations do Java (@PostMapping).
@app.post("/processar")
async def processar_imagem(file: UploadFile = File(...)):
    """
    Esta função recebe um arquivo, gera um hash e detecta objetos.
    """
    # Lendo o arquivo (No Python 3.8, o 'await' dentro de 'async def' é padrão)
    conteudo = await file.read()
    
    # Abrindo a imagem com a biblioteca PIL
    imagem = Image.open(io.BytesIO(conteudo))

    # GERANDO O HASH (A "Identidade" da imagem)
    # Isso resolve seu problema de "saber se a imagem já existe"
    hash_digital = str(imagehash.whash(imagem)) 

    # RODANDO O YOLO
    # results é uma lista de objetos com as detecções
    results = modelo(imagem)
    
    lista_objetos = []
    for r in results:
        for box in r.boxes:
            # Criamos um dicionário (parecido com um Map no Java ou JSON)
            dados = {
                "classe": modelo.names[int(box.cls)],
                "precisao": float(box.conf),
                "coordenadas": box.xyxy.tolist()[0] # [x1, y1, x2, y2]
            }
            lista_objetos.append(dados)

    # O FastAPI converte esse dicionário automaticamente para JSON
    return {
        "imagem_id": hash_digital,
        "objetos": lista_objetos,
        "total": len(lista_objetos)
    }
