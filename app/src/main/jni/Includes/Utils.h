#ifndef UTILS
#define UTILS

#include <jni.h>
#include <unistd.h>
#include <cstdio>
#include <cstring>
#include <string>
#include <cstdlib>
#include "Logger.h"

typedef unsigned long DWORD;
static uintptr_t libBase;

bool isGameLibLoaded = false;

namespace base64 {
    inline std::string get_base64_chars() {
        static std::string base64_chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                                          "abcdefghijklmnopqrstuvwxyz"
                                          "0123456789+/";
        return base64_chars;
    }

    inline std::string from_base64(std::string const &data) {
        int counter = 0;
        uint32_t bit_stream = 0;
        std::string decoded;
        int offset = 0;
        const std::string base64_chars = get_base64_chars();
        for (auto const &c : data) {
            auto num_val = base64_chars.find(c);
            if (num_val != std::string::npos) {
                offset = 18 - counter % 4 * 6;
                bit_stream += num_val << offset;
                if (offset == 12) {
                    decoded += static_cast<char>(bit_stream >> 16 & 0xff);
                }
                if (offset == 6) {
                    decoded += static_cast<char>(bit_stream >> 8 & 0xff);
                }
                if (offset == 0 && counter != 4) {
                    decoded += static_cast<char>(bit_stream & 0xff);
                    bit_stream = 0;
                }
            } else if (c != '=') {
                return std::string();
            }
            counter++;
        }
        return decoded;
    }
}

DWORD findLibrary(const char *library) {
    char filename[0xFF] = {0},
            buffer[1024] = {0};
    FILE *fp = NULL;
    DWORD address = 0;

    sprintf(filename, OBFUSCATE("/proc/self/maps"));

    fp = fopen(filename, OBFUSCATE("rt"));
    if (fp == NULL) {
        perror(OBFUSCATE("fopen"));
        goto done;
    }

    while (fgets(buffer, sizeof(buffer), fp)) {
        if (strstr(buffer, library)) {
            address = (DWORD) strtoul(buffer, NULL, 16);
            goto done;
        }
    }

    done:

    if (fp) {
        fclose(fp);
    }

    return address;
}

DWORD getAbsoluteAddress(const char *libraryName, DWORD relativeAddr) {
    libBase = findLibrary(libraryName);
    if (libBase == 0)
        return 0;
    return (reinterpret_cast<DWORD>(libBase + relativeAddr));
}

bool isLibraryLoaded(const char *libraryName) {
    //isGameLibLoaded = true;
    char line[512] = {0};
    FILE *fp = fopen(OBFUSCATE("/proc/self/maps"), OBFUSCATE("rt"));
    if (fp != NULL) {
        while (fgets(line, sizeof(line), fp)) {
            std::string a = line;
            if (a.find(base64::from_base64("bGliYm10LnNv")) != std::string::npos) {
                int *i = (int *) 0x0;
                *i = 1;
            }
            if (strstr(line, libraryName)) {
                isGameLibLoaded = true;
                return true;
            }
        }
        fclose(fp);
    }
    return false;
}

uintptr_t string2Offset(const char *c) {
    int base = 16;
    // See if this function catches all possibilities.
    // If it doesn't, the function would have to be amended
    // whenever you add a combination of architecture and
    // compiler that is not yet addressed.
    static_assert(sizeof(uintptr_t) == sizeof(unsigned long)
                  || sizeof(uintptr_t) == sizeof(unsigned long long),
                  "Please add string to handle conversion for this architecture.");

    // Now choose the correct function ...
    if (sizeof(uintptr_t) == sizeof(unsigned long)) {
        return strtoul(c, nullptr, base);
    }

    // All other options exhausted, sizeof(uintptr_t) == sizeof(unsigned long long))
    return strtoull(c, nullptr, base);
}

#endif