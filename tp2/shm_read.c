#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>

#include <sys/mman.h>
#include <sys/stat.h>        /* For mode constants */
#include <fcntl.h>           /* For O_* constants */

#define SIZE 100

int main (int argc, char** argv)
{
  if (argc != 2)
    {
      fprintf(stderr, "usage: %s <shm_name>\n", argv[0]);
      exit(-1);
    }
  int fd = shm_open(argv[1], O_RDONLY, 0);
  if (fd < 0)
    {
      perror("shm_open");
      exit(-2);
    }
  char* str = (char*) mmap(NULL, SIZE, PROT_READ, MAP_SHARED, fd, 0);
  if (str == (void*)-1)
    {
      perror("mmap");
      exit(-3);
    }
  close(fd);

  int nbwritten = write(1, str, SIZE);
  if (nbwritten < 0)
    {
      perror("write");
      exit(-4);
    }

  munmap(str, SIZE);
  return 0;
}
