//
// Created by linuxdev on 3/10/16.
//

#ifndef IMGPROCESSOR_H
#define IMGPROCESSOR_H

#include "androcl.h"

void refNR (unsigned char* bufIn, unsigned char* bufOut, int* info);
void openCLNR (unsigned char* bufIn, unsigned char* bufOut, int* info);

#endif //IMGPROCESSOR_H
