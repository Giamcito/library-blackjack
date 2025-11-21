#ifndef BLACKJACK_H
#define BLACKJACK_H

#include <stdint.h>
#include <stdbool.h>
#include <stdlib.h>

/* === Tipos básicos === */

typedef enum {
    CLUBS = 0,
    DIAMONDS,
    HEARTS,
    SPADES
} suit_t;

typedef enum {
    RANK_ACE = 1,
    RANK_2,
    RANK_3,
    RANK_4,
    RANK_5,
    RANK_6,
    RANK_7,
    RANK_8,
    RANK_9,
    RANK_10,
    RANK_JACK,
    RANK_QUEN,
    RANK_KING
} rank_t;

typedef struct {
    suit_t suit;
    rank_t rank;
} card_t;

/* === Mano (dinámica) === */
typedef struct {
    card_t *cards;
    size_t count;
    size_t capacity;
} hand_t;

/* === Mazo (puede tener varias barajas) === */
typedef struct {
    card_t *cards;
    int size;
    int top_index;
} deck_t;

/* === Jugador === */
typedef struct {
    hand_t hand;
    int balance;
    int bet;
    bool active;   /* true si sigue jugando (no bust ni fuera) */
} player_t;

/* === Dealer === */
typedef struct {
    hand_t hand;
} dealer_t;

/* === Funciones del mazo === */
void deck_init(deck_t *deck, int num_decks);
void deck_shuffle(deck_t *deck, unsigned int seed);
int deck_draw(deck_t *deck, card_t *out);
int deck_remaining(const deck_t *deck);
void deck_free(deck_t *deck);

/* === Funciones de mano === */
void hand_init(hand_t *h);
void hand_clear(hand_t *h);
int hand_add_card(hand_t *h, const card_t *c);
int hand_value(const hand_t *h);
bool hand_is_bust(const hand_t *h);
bool hand_is_blackjack(const hand_t *h);

/* === Juego === */
void dealer_play(deck_t *deck, dealer_t *dealer);
void bj_deal_initial(deck_t *deck, player_t *players, int n_players, dealer_t *dealer);
void bj_player_hit(deck_t *deck, player_t *p);

/* === Apuestas === */
void player_place_bet(player_t *p, int amount);
void settle_bets(player_t *players, int n_players, dealer_t *dealer);

/* === Utilidades === */
const char* card_to_string(const card_t *c);

#endif

