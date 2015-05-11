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

    it 'contains the CredentialError class' do
      expect(defined?(JrGoogleData::CredentialError)).to eq("constant")
    end

    it 'contains the RetrievalError class' do
      expect(defined?(JrGoogleData::RetrievalError)).to eq("constant")
    end
  end
end
