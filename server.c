#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>

#include <sys/types.h>
#include <sys/stat.h>

#define buffer_size 1024
#define port 3001

/*void client_handler(int client_socket,int client_addr)
{
    char recv_buffer[buffer_size];
    //char send_buffer[buffer_size];
    recv(client_socket, recv_buffer, buffer_size, 0);
    printf("%s: %s", recv_buffer);

}*/




int main()
{
    int server_socket, new_socket;
    //char message[buffer_size];

    struct sockaddr_in client_addr;
    struct sockaddr_in server_addr;
    //int client_addrlen = sizeof(client_addr);
    //int server_addrlen = sizeof(server_addr);
    socklen_t addrlen = sizeof(struct sockaddr_in);

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
        if (fork() == 0)
        {
            char recv_buffer[buffer_size];
            //char send_buffer[buffer_size];
            while(1)
            {
                memset(recv_buffer, 0, buffer_size);
                if(recv(new_socket, recv_buffer, buffer_size, 0)<= 0)
                {
                    printf("%s: disconected", inet_ntoa(client_addr.sin_addr));
                    break;
                }
                printf("%s: %s", inet_ntoa(client_addr.sin_addr), recv_buffer);
                write(new_socket, recv_buffer, strlen(recv_buffer));
            }

            exit(0);
        }

    }


    return 0;
}




