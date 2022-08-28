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

const val SandboxConfig = """
{
  "@type": "config.global",
  "dht": {
    "@type": "dht.config.global",
    "k": 10,
    "a": 10,
    "static_nodes": {
      "@type": "dht.nodes",
      "nodes": [
        {
          "@type": "dht.node",
          "id": {
            "@type": "pub.ed25519",
            "key": "3apS30kX0bjmaTg9O3XijqT4cZ+n+zNAKzf+9bDJkHE="
          },
          "addr_list": {
            "@type": "adnl.addressList",
            "addrs": [
              {
                "@type": "adnl.address.udp",
                "ip": 1091947629,
                "port": 25646
              }
            ],
            "version": 0,
            "reinit_date": 0,
            "priority": 0,
            "expire_at": 0
          },
          "version": -1,
          "signature": "ZYspxfhu1fRHKgkvByNnobNmx9yTFUmES6qZkUooBuHmoksZAcdAXalADUP6H8G0RYlJ8QaRjGE8beh4V9hHAg=="
        },
        {
          "@type": "dht.node",
          "id": {
            "@type": "pub.ed25519",
            "key": "z1YZtVmnuo4JxAnIXFLiMvA3RvZhP7TJM82S8bxV1Oo="
          },
          "addr_list": {
            "@type": "adnl.addressList",
            "addrs": [
              {
                "@type": "adnl.address.udp",
                "ip": 1091945203,
                "port": 8356
              }
            ],
            "version": 0,
            "reinit_date": 0,
            "priority": 0,
            "expire_at": 0
          },
          "version": -1,
          "signature": "xcsYwvrUDue1QEfedpwCh5qAgjXc3qMoKP7IBl91yYQQec2Is9TYZTGrVOuDDUF6dU8ey+CQ0bwTQFHIbB8cDg=="
        }
      ]
    }
  },
  "liteservers": [
    {
      "ip": -1573430149,
      "port": 4798,
      "id": {
        "@type": "pub.ed25519",
        "key": "GWyrpOKmxVgp7viY+hjjR5LrKI5O8zwheY+5gIUjZMo="
      }
    },
    {
      "ip": 822901272,
      "port": 7811,
      "id": {
        "@type": "pub.ed25519",
        "key": "eF2itktelj5g7nnJIaMW/RwAaE2Bzr1EMMrPAHDy3zA="
      }
    }
  ],
  "validator": {
    "@type": "validator.config.global",
    "zero_state": {
      "workchain": -1,
      "shard": -9223372036854775808,
      "seqno": 0,
      "root_hash": "ZNDXIYn4UXMFCwfL2UfKDX+ALn7b5f//UiPBF2vS/PY=",
      "file_hash": "uLPQA4p8yt3bIUAaFDSXPH0yp3R6kTpzwp9XpSkGurU="
    },
    "init_block": {
      "workchain": -1,
      "shard": -9223372036854775808,
      "seqno": 0,
      "root_hash": "ZNDXIYn4UXMFCwfL2UfKDX+ALn7b5f//UiPBF2vS/PY=",
      "file_hash": "uLPQA4p8yt3bIUAaFDSXPH0yp3R6kTpzwp9XpSkGurU="
    }
  }
}
"""
