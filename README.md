# JrGoogleData

TODO: Write a gem description

## Installation

Add this line to your application's Gemfile:

```ruby
gem 'jrgoogledata'
```

And then execute:

    $ bundle

Or install it yourself as:

    $ gem install jrgoogledata

## Usage

More to follow.
This is a work in progress.

Read the specs to see how to use this library (but you do that anyway don't you?).

You will need to provide credentials.  The json file that the developer
console provides is perfect for this.

```json
{
  "private_key_id": "8373...67c5",
  "private_key": "-----BEGIN PRIVATE KEY-----\nMIICe...lr9Q+FJ\n-----END PRIVATE KEY-----\n",
  "email": "4979...@developer.gserviceaccount.com",
  "client_id": "4...1k.apps.googleusercontent.com",
  "scopes": "https://docs.google.com/feeds/ https://docs.googleusercontent.com/ https://www.googleapis.com/auth/drive https://spreadsheets.google.com/feeds/",
  "type": "service_account"
}
```

You will also need to enable the Drive API in the developer console.

## Contributing

1. Fork it ( https://github.com/[my-github-username]/jrgoogledata/fork )
2. Create your feature branch (`git checkout -b my-new-feature`)
3. Commit your changes (`git commit -am 'Add some feature'`)
4. Push to the branch (`git push origin my-new-feature`)
5. Create a new Pull Request
