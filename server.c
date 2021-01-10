#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <unistd.h>
#include <pthread.h>
#include <dirent.h>

#include <sys/types.h>
#include <sys/stat.h>

#define buffer_size 1024
#define output_buffer_size 2000
#define port 3000
#define max_clients 50

#define welcome_msg "---------------------------------------------Welcome---------------------------------------------\n"

/*void client_handler(int client_socket,int client_addr)
{
    char recv_buffer[buffer_size];
    //char send_buffer[buffer_size];
    recv(client_socket, recv_buffer, buffer_size, 0);
    printf("%s: %s", recv_buffer);
}*/
int curr_clients_amount = 0;

typedef struct{
    struct sockaddr_in address;
    int id;
    int socket;
    char name[32];

} client_struct;


client_struct clients[max_clients];

/*
void init_client(struct client_struct *client, int id, struct sockaddr_in address, int socket, char* name){
    client->address = address;
    client->id = id;
    client->socket = socket;
    client->name = name;
}
*/

void send_to_all(char *msg, int client_id){
    for(int i = 0; i < max_clients; i++){
        if(clients[i].id >= 1){
            if(clients[i].id != client_id){
                write(clients[i].socket, msg, strlen(msg));
            }
        }
    }
}


void remove_client(int client_id){
    int i = 0;
    while(clients[i].id != client_id)
        i++;
    clients[i] = clients[curr_clients_amount-1];
    clients[curr_clients_amount-1].id = -1;
    curr_clients_amount--;
}


void *handle_client(void *arg){
    char recv_buffer[buffer_size];
    char output_buffer[output_buffer_size];
    client_struct *client = (client_struct *)arg;


    while(1)
    {
        memset(recv_buffer, 0, buffer_size);
        if(recv(client->socket, recv_buffer, buffer_size, 0)<= 0)
        {
            memset(output_buffer, 0 ,output_buffer_size);
            sprintf(output_buffer, "%s disconnected\n", client->name);
            close(client->socket);
            printf("%s(%s) disconnected\n", inet_ntoa(client->address.sin_addr), client->name);
            send_to_all(output_buffer, client->id);
            break;
        }
        printf("%s(%s): %s\n", inet_ntoa(client->address.sin_addr), client->name, recv_buffer);
        sprintf(output_buffer, "%s: %s", client->name, recv_buffer);
        send_to_all(output_buffer, client->id);
    }
    remove_client(client->id);
    pthread_exit(NULL);
}

    void handle_download(int new_socket)
    {
        char path[512];
        char full_path[512];
        struct stat fileinfo;
        FILE* file;
        long file_length, send, all_send, read;

        unsigned char buffer[buffer_size];
        DIR *d;
        struct dirent *dir;
        d = opendir("./files");
        if (d)
        {
            printf("sending file list\n");
            while ((dir = readdir(d)) != NULL)
            {
                sprintf(buffer, " - %s\n", dir->d_name);
                if(strcmp(buffer, " - .\n") == 0 || strcmp(buffer, " - ..\n") == 0)
                {
                    continue;
                }
                //printf("%s", file_buffer);
                write(new_socket, buffer, strlen(buffer));
            }
            closedir(d);
        }

        memset(path, 0, 512);
        if (recv(new_socket, path, 512, 0) <= 0)
        {
            printf("Couldn't read path\n");
            return;
        }
        sprintf(full_path, "./files/%s", path);
        full_path[strlen(full_path)-1] = 0;
        printf("buffor sciezki - (%s)\n", full_path);
        if (stat(full_path, &fileinfo) < 0)
        {
            printf("Couldn't find file\n");
            return;
        }
        else
        {
            printf("jest w pyte :D\n");
        }
            if (fileinfo.st_size == 0)
        {
            printf("File size: 0\n");
            return;
        }
        printf("File size: %d\n", fileinfo.st_size);

        file_length = fileinfo.st_size;
        all_send = 0;

        file = fopen(full_path, "rb");
        if(file == NULL)
        {
            printf("Couldn't open file\n");
            return;
        }
        else
        {
            printf("otwarcie pliku chyba dziala\n");
        }

        while (all_send < file_length)
        {
            memset(buffer, 0, 1024);
            read = fread(buffer, 1, 1024, file);
            send = write(new_socket, buffer, read);
            if (read != send)
            {
                break;
            }
            all_send += send;
        }
        if (all_send == file_length)
        {

            printf("Plik wyslany poprawnie\n");
        }
        else
        {
            printf("Blad przy wysylaniu pliku\n");
        }

        fclose(file);
    }



int main()
{
    int server_socket, new_socket;
    int id = 1;

    struct sockaddr_in client_addr;
    struct sockaddr_in server_addr;

    pthread_t tid;

    //int client_addrlen = sizeof(client_addr);
    //int server_addrlen = sizeof(server_addr);
    socklen_t addrlen = sizeof(struct sockaddr_in);
    char recv_name[32];

    server_socket = socket(PF_INET, SOCK_STREAM, 0);
    server_addr.sin_family = AF_INET;
    server_addr.sin_port = htons(port);
    server_addr.sin_addr.s_addr = INADDR_ANY;
    memset(server_addr.sin_zero, 0, sizeof(server_addr.sin_zero));
    memset(client_addr.sin_zero, 0, sizeof(client_addr.sin_zero));

    printf("Server info: %s:%u\n",
        inet_ntoa(server_addr.sin_addr),
        ntohs(server_addr.sin_port)
        );

    if (bind(server_socket, (struct sockaddr*) &server_addr, addrlen) < 0)
    {
        printf("Main: bind failed\n");
        return 1;
    }

    if (listen(server_socket, 10) < 0)
    {
        printf("Main: Listen failed\n");
        return 1;
    }

    while(1)
    {
        socklen_t addrlen = sizeof(struct sockaddr_in);
        new_socket = accept(server_socket, (struct sockaddr*) &client_addr, &addrlen);

        if (new_socket < 0)
        {
            printf("Accept failed\n");
            continue;
        }

        printf("Connection from %s:%u\n",
            inet_ntoa(client_addr.sin_addr),
            ntohs(client_addr.sin_port)
            );

        if (curr_clients_amount > 49){
            printf("Connection limit reached. Closing connection with %s:%u\n .",
            inet_ntoa(client_addr.sin_addr),
            ntohs(client_addr.sin_port)
            );
            close(new_socket);
            continue;
        }

        memset(recv_name, 0, 32);
        recv(new_socket, recv_name, 32, 0);
        recv_name[strlen(recv_name)-1] = '\0';

        if(strcmp(recv_name, "download") == 0)
        {
            if (fork() == 0)
            {
                fflush(stdout);
                handle_download(new_socket);
            }
        }
        else
        {

        write(new_socket, welcome_msg, strlen(welcome_msg));


        clients[curr_clients_amount].address = client_addr;
        clients[curr_clients_amount].id = id;
        strcpy(clients[curr_clients_amount].name, recv_name);
        clients[curr_clients_amount].socket = new_socket;
        id++;
        curr_clients_amount++;

        pthread_create(&tid, NULL, &handle_client, &clients[curr_clients_amount-1]);
        }


    }
    return 0;
}
