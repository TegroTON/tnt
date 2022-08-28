/*
 * (T)ON (N)FT (T)ool - all-in-one utility to work with NFTs on The Open Network
 * Copyright (c) 2022
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.libermall.tnt.config

const val TestnetConfig = """
{
  "liteservers": [
    {
      "ip": 1959448750,
      "port": 51281,
      "id": {
        "@type": "pub.ed25519",
        "key": "hyXd2d6yyiD/wirjoraSrKek1jYhOyzbQoIzV85CB98="
      }
    },
    {
      "ip": 1097633201,
      "port": 17439,
      "id": {
        "@type": "pub.ed25519",
        "key": "0MIADpLH4VQn+INHfm0FxGiuZZAA8JfTujRqQugkkA8="
      }
    },
    {
      "ip": 1097649206,
      "port": 29296,
      "id": {
        "@type": "pub.ed25519",
        "key": "p2tSiaeSqX978BxE5zLxuTQM06WVDErf5/15QToxMYA="
      }
    }
  ],
  "dht": {
    "a": 3,
    "k": 3,
    "static_nodes": {
      "nodes": [
        {
          "@type": "dht.node",
          "id": {
            "@type": "pub.ed25519",
            "key": "yak8NHUpgpPJ96j90+dFjKOEmNqJo3zVJLu9J3F8ncI="
          },
          "addr_list": {
            "@type": "adnl.addressList",
            "addrs": [
              {
                "@type": "adnl.address.udp",
                "ip": -1903905862,
                "port": 56040
              }
            ],
            "version": 0,
            "reinit_date": 0,
            "priority": 0,
            "expire_at": 0
          },
          "version": -1,
          "signature": "2A9PNArDWYS3ZKgKbGFr7RxeE/hG8iBYSlNuqNI1q3CUHgFgpTSTG3xtEY86OzuviNz5oPXzET04T63S7FbgCA=="
        },
        {
          "@type": "dht.node",
          "id": {
            "@type": "pub.ed25519",
            "key": "+U2zJXltAQVbgOepQdkam7sJAAdDboxlwvkTG4Oih04="
          },
          "addr_list": {
            "@type": "adnl.addressList",
            "addrs": [
              {
                "@type": "adnl.address.udp",
                "ip": 1959448750,
                "port": 60982
              }
            ],
            "version": 0,
            "reinit_date": 0,
            "priority": 0,
            "expire_at": 0
          },
          "version": -1,
          "signature": "sVdFJxk2SG/Wjh35x4yKnuZzzXyQgmuK/FLy0wge3qGHCs6Wg5HhhxU1WnpgXPLIGXfjVKN7Ud0L1APlgVmuDg=="
        },
        {
          "@type": "dht.node",
          "id": {
            "@type": "pub.ed25519",
            "key": "AXJfGheUX49go0slEHG9oJK2qwYjcsclvsigLgvb9a0="
          },
          "addr_list": {
            "@type": "adnl.addressList",
            "addrs": [
              {
                "@type": "adnl.address.udp",
                "ip": -1185526601,
                "port": 35692
              }
            ],
            "version": 0,
            "reinit_date": 0,
            "priority": 0,
            "expire_at": 0
          },
          "version": -1,
          "signature": "E/iTX659lsmda79MSlO2U0oFJnosYnVHh7o/CPZ5ZPF7rNSThK/iWH/gzgsrhi+ULXa2KgvSlb/YXhJC0G2iDQ=="
        },
        {
          "@type": "dht.node",
          "id": {
            "@type": "pub.ed25519",
            "key": "fO6cFYRCRrD+yQzOJdHcNWpRFwu+qLhQnddLq0gGbTs="
          },
          "addr_list": {
            "@type": "adnl.addressList",
            "addrs": [
              {
                "@type": "adnl.address.udp",
                "ip": 1097633201,
                "port": 7201
              }
            ],
            "version": 0,
            "reinit_date": 0,
            "priority": 0,
            "expire_at": 0
          },
          "version": -1,
          "signature": "o/rhtiUL3rvA08TKBcCn0DCiSjsNQdAv41aw7VVUig7ubaqJzYMv1cW3qMjxvsXn1BOugIheJm7voA1/brbtCg=="
        },
        {
          "@type": "dht.node",
          "id": {
            "@type": "pub.ed25519",
            "key": "UAbZu98NLBiRzE/4YIgiryZN+cAxz1mfH8C9kDkZMNQ="
          },
          "addr_list": {
            "@type": "adnl.addressList",
            "addrs": [
              {
                "@type": "adnl.address.udp",
                "ip": -1185526389,
                "port": 20629
              }
            ],
            "version": 0,
            "reinit_date": 0,
            "priority": 0,
            "expire_at": 0
          },
          "version": -1,
          "signature": "2EvSD95SVEHyi1Gzfetj20fDRoI0heWvZtBwuMGu6+B+gD8UyGIhUPyWvcCQ0pU8UBLO42jzYBDLtWkHdMhqBg=="
        },
        {
          "@type": "dht.node",
          "id": {
            "@type": "pub.ed25519",
            "key": "U14blgn1BJoD1VYMQhvPVdV8Ad593z8iAoB6hqi3kIs="
          },
          "addr_list": {
            "@type": "adnl.addressList",
            "addrs": [
              {
                "@type": "adnl.address.udp",
                "ip": -1587440875,
                "port": 30825
              }
            ],
            "version": 0,
            "reinit_date": 0,
            "priority": 0,
            "expire_at": 0
          },
          "version": -1,
          "signature": "5kYHn3rVp3IC+wrzseOjRlrRr+ajisWKojHr2pznKNMxF7woWmD5QToIkTVXC9ZMErBts5wbbltb2f1AhGroBA=="
        },
        {
          "@type": "dht.node",
          "id": {
            "@type": "pub.ed25519",
            "key": "fVIJzD9ATMilaPd847eFs6PtGSB67C+D9b4R+nf1+/s="
          },
          "addr_list": {
            "@type": "adnl.addressList",
            "addrs": [
              {
                "@type": "adnl.address.udp",
                "ip": 1097649206,
                "port": 29081
              }
            ],
            "version": 0,
            "reinit_date": 0,
            "priority": 0,
            "expire_at": 0
          },
          "version": -1,
          "signature": "wH0HEVT6yAfZZAoD5bF6J3EZWdSFwBGl1ZpOfhxZ0Bp2u52tv8OzjeH8tlZ+geMLTG50Csn5nxSKP1tswTWwBg=="
        }
      ],
      "@type": "dht.nodes"
    },
    "@type": "dht.config.global"
  },
  "@type": "config.global",
  "validator": {
    "zero_state": {
      "file_hash": "Z+IKwYS54DmmJmesw/nAD5DzWadnOCMzee+kdgSYDOg=",
      "seqno": 0,
      "root_hash": "gj+B8wb/AmlPk1z1AhVI484rhrUpgSr2oSFIh56VoSg=",
      "workchain": -1,
      "shard": -9223372036854775808
    },
    "@type": "validator.config.global",
    "init_block": {
      "file_hash": "Z+IKwYS54DmmJmesw/nAD5DzWadnOCMzee+kdgSYDOg=",
      "seqno": 0,
      "root_hash": "gj+B8wb/AmlPk1z1AhVI484rhrUpgSr2oSFIh56VoSg=",
      "workchain": -1,
      "shard": -9223372036854775808
    }
  }
}
"""
