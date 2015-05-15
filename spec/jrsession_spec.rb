require 'spec_helper'

module JrGoogleData
  describe Session do
    context 'its public api' do
      it 'has a constructor that takes one argument' do
        expect{described_class.new}.to raise_error(
            ArgumentError, /wrong number of arguments/
          )
      end

      it 'has a constructor that takes a hash argument' do
        expect{described_class.new('email')}.to raise_error(
            ArgumentError, /argument options hash is missing/
          )
      end

      it 'has a constructor that takes a hash argument with an :email key' do
        hash = {'email' =>'foo@bar.com', 'private_key' => 'sekret', 'scopes' => '', 'private_key_id' => '837306608'}
        expect{described_class.new(hash)}.to raise_error(
            ArgumentError, /hash key value :email is missing/
          )
      end

      it 'has a constructor that takes a hash argument with an :private_key key' do
        hash = {email: 'foo@bar.com', 'private_key' => 'sekret', 'scopes' => '', 'private_key_id' => '837306608'}
        expect{described_class.new(hash)}.to raise_error(
            ArgumentError, /hash key value :private_key is missing/
          )
      end

      it 'has a constructor that takes a hash argument with an :scopes key' do
        hash = {email: 'foo@bar.com', private_key: 'sekret', 'scopes' => '', private_key_id: '837306608'}
        expect{described_class.new(hash)}.to raise_error(
            ArgumentError, /hash key value :scopes is missing/
          )
      end

      it 'has a constructor that takes a hash argument with an :private_key_id key' do
        hash = {email: 'foo@bar.com', private_key: 'sekret', scopes: 'https://docs.google.com/feeds/', 'private_key_id' => '837306608'}
        expect{described_class.new(hash)}.to raise_error(
            ArgumentError, /hash key value :private_key_id is missing/
          )
      end

      it 'has a constructor that throws an error if the supplied private_key is malformed' do
        hash = {email: 'foo@bar.com', private_key: 'sekret', scopes: 'https://docs.google.com/feeds/', private_key_id: '837306608'}
        expect{described_class.new(hash)}.to raise_error(
            JrGoogleData::CredentialError, /Unexpected exception reading PKCS data/
          )
      end

      it 'has a constructor that authenticates correctly to return an instance' do
        expect{described_class.new(Creds)}.not_to raise_error
      end
    end

    context 'an instance' do
      let(:session) { described_class.new(Creds) }

      it 'has public workbook_by_id method' do
        expect(session).to respond_to(:workbook_by_id)
      end

      it 'has public workbook_by_title method' do
        expect(session).to respond_to(:workbook_by_title)
      end

      it 'using an id, it raises an error when a wookbook is not found' do
        expect{session.workbook_by_id('16QOG3WKn_WPUt1EYLE5_VkTPU82yDvpMqrc7jr40jjs')}.to raise_error(
            JrGoogleData::ReadError, /ServiceException: Internal Server Error/
          )
      end

      it 'retrieves a workbook object using an id' do
        workbook = session.workbook_by_id('16QOG3WKn_WPUt1EYLE5_VkTPU82yDvpMqrc7jr40iis')
        expect(workbook).not_to eq(nil)
      end

      it 'using a title, it raises an error when a wookbook is not found' do
        expect{session.workbook_by_title('Test-colors')}.to raise_error(
            JrGoogleData::ReadError, /File not found with title/
          )
      end

      it 'retrieves a workbook object using a title' do
        workbook = session.workbook_by_title('Test-colours')
        expect(workbook).not_to eq(nil)
      end
    end
  end
end
