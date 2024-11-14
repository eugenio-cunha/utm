# Verifique se o comando ponto está disponível
if ! command -v dot &> /dev/null
then
    echo "O comando 'ponto' não foi encontrado. Isso é necessário para gerar SVGs a partir dos arquivos Graphviz."
    echo "Instruções de instalação:"
    echo "  - No macOS: você pode instalar o Graphviz usando o Homebrew com o comando: 'brew install graphviz'"
    echo "  - No Ubuntu: você pode instalar o Graphviz usando APT com o comando: 'sudo apt-get install graphviz'"
    exit 1
fi

# Verifique se há uma versão do grep que suporte Perl regex.
# No MacOS, o sistema operacional instalado grep não suporta Perl regex, então verifique a existência do
# Versão GNU, que é prefixada com 'g' para distingui-la da versão instalada do sistema operacional.
    if grep -P "" /dev/null > /dev/null 2>&1; then
    GREP_COMMAND=grep
elif command -v ggrep &> /dev/null; then
    GREP_COMMAND=ggrep
else
    echo "Você não tem uma versão do 'grep' instalada que suporte expressões regulares Perl."
    echo "No MacOS você pode instalar um usando Homebrew com o comando: 'brew install grep'"
    exit 1
fi

# Inicialize um array para armazenar módulos excluídos
excluded_modules=()

# Analisar argumentos de linha de comando para módulos excluídos
while [[ $# -gt 0 ]]; do
    case "$1" in
        --exclude-module)
            excluded_modules+=("$2")
            shift # Argumento passado
            shift # Valor passado
            ;;
        *)
            echo "Unknown parameter passed: $1"
            exit 1
            ;;
    esac
done

# Obtenha os caminhos do módulo
module_paths=$(${GREP_COMMAND} -oP 'include\("\K[^"]+' settings.gradle.kts)

# Certifique-se de que o diretório de saída exista
mkdir -p docs/images/graphs/

# Função para verificar e criar um README.md para módulos que não possuem um.
check_and_create_readme() {
    local module_path="$1"
    local file_name="$2"

    local readme_path="${module_path:1}" # Remove dois pontos iniciais
    readme_path=${readme_path//:/\/} # Substitua dois pontos por barras
    readme_path="${readme_path}/README.md" # Anexar o nome do arquivo

    # Verifique se README.md existe e crie-o se não
    if [[ ! -f "$readme_path" ]]; then
        echo "Creating README.md for ${module_path}"

        # Determine a profundidade do módulo com base no número de dois pontos
        local depth=$(awk -F: '{print NF-1}' <<< "${module_path}")

        # Construa o caminho relativo da imagem com o número correto de "../"
        local relative_image_path="../"
        for ((i=1; i<$depth; i++)); do
            relative_image_path+="../"
        done
        relative_image_path+="docs/images/graphs/${file_name}.svg"

        echo "# Módulo ${module_path}" > "$readme_path"
        echo "## Gráfico de dependência" >> "$readme_path"
        echo "![Gráfico de dependência](${relative_image_path})" >> "$readme_path"
    fi
}

# Loop através de cada caminho do módulo
echo "$module_paths" | while read -r module_path; do
    # Verifique se o módulo está na lista de excluídos
    if [[ ! " ${excluded_modules[@]} " =~ " ${module_path} " ]]; then
        # Derive o nome do arquivo do caminho do módulo
        file_name="dep_graph${module_path//:/_}" # Substitua dois pontos por sublinhados
        file_name="${file_name//-/_}" # Substitua travessões por sublinhados

        check_and_create_readme "$module_path" "$file_name"

        # Gere o arquivo .gv em um local temporário
        # </dev/null é usado para impedir que ./gradlew consuma entradas que encerram prematuramente o loop while
        ./gradlew generateModulesGraphvizText \
          -Pmodules.graph.output.gv="/tmp/${file_name}.gv" \
          -Pmodules.graph.of.module="${module_path}" </dev/null

        # Converta para SVG usando ponto, remova comentários desnecessários e reformate
        dot -Tsvg "/tmp/${file_name}.gv" > "docs/images/graphs/${file_name}.svg"

        # Remova o arquivo temporário .gv
        rm "/tmp/${file_name}.gv"
    fi
done