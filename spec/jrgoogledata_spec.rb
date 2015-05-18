require 'spec_helper'

describe JrGoogleData do
  context 'the basics' do
    it 'has a version number' do
      expect(JrGoogleData::VERSION).not_to be nil
    end

    it 'contains the Session class' do
      expect(defined?(JrGoogleData::Session)).to eq("constant")
    end

    it 'contains the Workbook class' do
      expect(defined?(JrGoogleData::Workbook)).to eq("constant")
    end

    it 'contains the Worksheet class' do
      expect(defined?(JrGoogleData::Worksheet)).to eq("constant")
    end

    it 'contains the ListQuery class' do
      expect(defined?(JrGoogleData::ListQuery)).to eq("constant")
    end

    it 'contains the Row class' do
      expect(defined?(JrGoogleData::Row)).to eq("constant")
    end

    it 'contains the CredentialError class' do
      expect(defined?(JrGoogleData::CredentialError)).to eq("constant")
    end

    it 'contains the ReadError class' do
      expect(defined?(JrGoogleData::ReadError)).to eq("constant")
    end

    it 'contains the WriteError class' do
      expect(defined?(JrGoogleData::WriteError)).to eq("constant")
    end
  end
end
