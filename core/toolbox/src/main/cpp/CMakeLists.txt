cmake_minimum_required(VERSION 3.22.1)
project("toolbox")

# Declara a biblioteca turbojpeg como IMPORTED apenas se ainda não existir
if(NOT TARGET turbojpeg)
    add_library(turbojpeg SHARED IMPORTED)
    set_target_properties(turbojpeg PROPERTIES
            IMPORTED_LOCATION
            ${CMAKE_CURRENT_LIST_DIR}/jniLibs/${ANDROID_ABI}/libturbojpeg.so
    )
endif()

# Cria a biblioteca principal do seu módulo
add_library(${CMAKE_PROJECT_NAME} SHARED
        toolbox.cpp
)

# Diretórios de headers
target_include_directories(${CMAKE_PROJECT_NAME} PRIVATE
        ${CMAKE_SOURCE_DIR}/header
        ${CMAKE_SOURCE_DIR}/header/libjpeg-turbo
)

# Bibliotecas nativas Android
find_library(log-lib log)
find_library(android-lib android)

# Vincula as bibliotecas
target_link_libraries(${CMAKE_PROJECT_NAME}
        ${android-lib}
        turbojpeg
        ${log-lib}
)
