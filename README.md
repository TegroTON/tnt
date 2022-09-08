🧨 TNT
==

**TNT** is an all-in-one command line tool to query, edit, and mint new
Non-Fungible Tokens on [The Open Network](https://ton.org). Written in Kotlin
it enables fully cross-platform experience with no extra dependencies and does
not rely on any third-party services. Out-of-the-box multiple network
configurations are provided, for easy use in mainnet (live TON network) as well
as [testnet](https://ton.org/docs/#/testnet/), and
[sandbox](https://tondev.org/sandbox) for experiments and development.

> Disclaimer: **TNT** is software in early development, based on beta versions
> of underlying tools and libraries. Although possible, it is strongly
> discouraged to use its modifying capabilities (commands such as `tnt mint`)
> in live TON mainnet network, as it may result in loss of funds.
>
> **TNT** is provided WITHOUT ANY WARRANTY

# Getting Started

**TNT** features multiple commands TODO

It is important to note use of `--network=mainnet|testnet|sandbox` option in
following examples to explicitly specify target network, as attempting to call
an operation on a contract residing in a different network will inevitably fail.
This option defaults to `testnet`.

## Getting NFT Collection Information

In order to get NFT collection info, such as number of items in it or its owner
account, `tnt collection` command is used as follows:

```shell
$ tnt --network=mainnet collection 'EQAadrsHePbHk-v7KtM4_jrX0HTlMYfP9ZGtlLgn590D7-SC'
                    Collection Properties                     
┌─────────┬──────────────────────────────────────────────────┐
│ Address │ EQAadrsHePbHk-v7KtM4_jrX0HTlMYfP9ZGtlLgn590D7-SC │
├─────────┼──────────────────────────────────────────────────┤
│ Size    │ 7778                                             │
├─────────┼──────────────────────────────────────────────────┤
│ Owner   │ EQDVF5iErB47ryuH2HRBrbVIm-EKrb5WdTStrGEM8F9i77qB │
└─────────┴──────────────────────────────────────────────────┘
                            Collection Content                            
┌──────┬─────────────────────────────────────────────────────────────────┐
│ Kind │ off-chain                                                       │
├──────┼─────────────────────────────────────────────────────────────────┤
│ Uri  │ https://s.getgems.io/nft/b/c/626a922e5e67c1f424154711/meta.json │
└──────┴─────────────────────────────────────────────────────────────────┘
```

## Getting NFT Item Information

To get information about a particular NFT item, whether or not it belongs to
any specific collection, use `tnt item`:

```shell
$ tnt --network=mainnet item 'EQA9V87ROI2DZqm65Jj-CIaOMF4RgElzOge0YgktWqyOJO1x' 
                         Item Properties                          
┌─────────────┬──────────────────────────────────────────────────┐
│ Address     │ EQA9V87ROI2DZqm65Jj-CIaOMF4RgElzOge0YgktWqyOJO1x │
├─────────────┼──────────────────────────────────────────────────┤
│ Initialized │ true                                             │
├─────────────┼──────────────────────────────────────────────────┤
│ Index       │ 69                                               │
├─────────────┼──────────────────────────────────────────────────┤
│ Collection  │ EQAadrsHePbHk-v7KtM4_jrX0HTlMYfP9ZGtlLgn590D7-SC │
├─────────────┼──────────────────────────────────────────────────┤
│ Owner       │ EQChmtQM0oJX01hIY-9ej_whDeAWqtz_UtqlnDJJBpUszX28 │
└─────────────┴──────────────────────────────────────────────────┘
                           Item Content                           
┌──────┬────────────────────────────────────────────────────────────────────┐
│ Kind │ off-chain                                                          │
├──────┼────────────────────────────────────────────────────────────────────┤
│ Uri  │ https://s.getgems.io/nft/b/c/626a922e5e67c1f424154711/69/meta.json │
└──────┴────────────────────────────────────────────────────────────────────┘
```

# Minting items and collections

> Note: This is a modifying operation, that will send actual transactions to
> the chosen network. Certain amount of toncoins is required to perform these
> operations, it is strongly recommended to use free test coins provided by
> faucet bots in testnet or sandbox.

Unlike other tools used for mint of NFT items and collections, **TNT** deploys
a single-use wallet contract for each invocation of `tnt mint` command. Main
reason for this is security, as it eliminates the need of passing around secret
keys or mnemonic seed phrases. Whenever an NFT entity is deployed, ownership
is then transferred to an actual owner account, that is assumed to have its
secrets stored in a secure place. Another big reason is the ability to use
more efficient contracts for deployment, such as high-load wallets, speeding
up deployment of large collections. Importantly for testing, it also means
that mint of the same entity multiple times will result in a new address being
generated each time.

On the other hand, because a new account is used each time, insufficient
balance or any failure during **TNT**'s run may result in funds being
permanently locked in an inaccessible contract. Failure recovery functionality
is planned to be implemented in the future, when this tool reaches its first
stable release.

It is also important to note that at the moment there is no way to estimate
processing fees required to run smart-contracts on TON, and because of this
it is up to the user to provide sufficient amount of funds for this tool to
operate. Approximate amounts are currently specified as constants, and can be
modified by the user. As a general rule of thumb, minting a single item,
whether standalone or in collection, will cost about ~0.3 TON. This amount
includes worst case scenario blockchain fees and was choosen to be a safe
default, in reality it costs less than 0.2 TON.

## Specification file

For better portability, **TNT** reads desired item/collection properties from
a specification JSON file. This means that the same collection may be deployed
in testing network during development, and then simply minted according to the
same specification in mainnet.

Please note, before **TNT** reaches stable version, specification format is a
subject to change. Original files of the following examples can be found in the
`examples` directory.

### Standalone item example specification

Here's an example of a specification, that will mint a standalone (meaning it
does not belong to any collection) item when passed to `tnt mint`

```json
{
  "entities": [
    {
      "type": "item",
      "index": 69,
      "owner": "EQDpl_EDsAwIME91TvXCi6xsM_7dYaB4vZoDbcu_QHmldeUI",
      "content": {
        "type": "full_snake",
        "value": "https://s.getgems.io/nft/b/c/626a922e5e67c1f424154711/69/meta.json"
      }
    }
  ]
}
```

*entities* - an array of NFT entities (standalone items/collections) to mint.
Single specification may contain multiple entities of any kind.

*type* - must be "item" for standalone items.

*index* - index of an item in the collection. Makes no difference for
standalone items, and it is recommended to set it to "0" in this case.

*owner* - contract to be assigned as the owner of this item. After successful
mint a transaction will be sent to this address. Base64(url) and raw addresses
are supported, with special value `"none"` indicating empty owner. This will
essentially "burn" this item, as it will not be possible to ever change its
owner again.

*content* - item content, its metadata. In this case it is a `"full_snake"`
content kind, meaning it is a string containing a URL to off-chain JSON file
that is encoded in "snake data" format.

For more advanced users, `"cell"` kind of data may be used, where value is's a
base64-encoded Bag-of-Cells with a single root cell that will be used as item's
content.

```json
{
  "type": "cell",
  "value": "base64-encoded BoC with a single root cell"
}
```

To mint this standalone item, use the following command:

```shell
$ tnt --network=testnet mint examples/lonely_dog.json
```

And proceed with the instructions. You will have to send at least 0.3 TON to
the newly generated wallet address in testnet.

### Collection of items

For this, a slightly different specification is used:

```json
{
  "entities": [
    {
      "type": "collection",
      "collection_content": {
        "type": "full_snake",
        "value": "https://s.getgems.io/nft/b/c/626a922e5e67c1f424154711/meta.json"
      },
      "common_content": {
        "type": "naked_snake",
        "value": "https://s.getgems.io/nft/b/c/626a922e5e67c1f424154711/"
      },
      "owner": "EQDpl_EDsAwIME91TvXCi6xsM_7dYaB4vZoDbcu_QHmldeUI",
      "royalty": {
        "numerator": 69,
        "denominator": 1000,
        "destination": "EQDpl_EDsAwIME91TvXCi6xsM_7dYaB4vZoDbcu_QHmldeUI"
      },
      "items": [
        {
          "owner": "EQDpl_EDsAwIME91TvXCi6xsM_7dYaB4vZoDbcu_QHmldeUI",
          "content": {
            "type": "naked_snake",
            "value": "1/meta.json"
          }
        }
        ...
      ]
    }
  ]
}
```

*entities* - same as before, single specification may contain multiple items
and collections to mint.

*type* - must be `"collection"`

*collection_content* - metadata of this particular collection contract. Note
the use of `"full_snake"` kind, it is important as wrong encoding may result in
client applications not being able to read collection content properly.

*common_content* - part of metadata that is common between all items of this
collection. Usually this is the longest common part of URL to item metadata.
Note the use of `"naked_snake"` kind, it is important because collection smart
contract concatenates common content, with individual item content via its get
method, producing valid "full_snake" snake data for the clients.

*owner* - an address of account that will be able to modify collection metadata
as well as mint more items.

*royalty* - this section contains information about royalties from each sale of
any item in the collection.

*royalty.numerator*, *royalty.denominator* - these two properties specify
percentage that will be sent to royalty holder on a sale. Maximum is 15%
Percentage is computed as numerator/denominator, in this case it is 6.9%.

*royalty.destination* - address of an account that will receive the royalty.
It is possible to set it to "none", as well as setting numerator and
denominator to 0, which will essentially disable royalty functionality of the
smart-contract.

*items* - an array of every item in the collection, specifying item's owner and
content. Note that item order matters, first item is assigned index of 0, next
has index of 1, and so on. **TNT** always obeys the original order of items.

In order to mint this collection, use the following command:

```shell
$ tnt --network=testnet mint examples/tegro_dog.json
```

Proceed according to instructions, you will have to send at least 3 TON to the
newly generated single-use address in testnet.
