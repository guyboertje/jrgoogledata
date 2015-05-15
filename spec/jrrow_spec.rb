require 'spec_helper'

module JrGoogleData
  describe Row do
    let(:session)   { a_session() }
    let(:worksheet) { a_workbook(session).worksheet_by_title('Main') }
    let(:instance)  { worksheet.get_rows(qry).first }

    context 'its public api' do
      let(:instance) { described_class.new }

      it 'has a constructor that takes no arguments' do
        expect{described_class.new}.not_to raise_error
      end

      it 'has an inspect method' do
        expect(instance).to respond_to(:inspect)
      end

      it 'has an merge method' do
        expect(instance).to respond_to(:merge)
      end

      it 'has an modify method' do
        expect(instance).to respond_to(:modify)
      end

      it 'has an to_hash method' do
        expect(instance).to respond_to(:to_hash)
      end

      it 'has an update method' do
        expect(instance).to respond_to(:update)
      end
    end

    context 'an instance' do
      let(:qry) { worksheet.new_list_query.add_start_index(1).add_max_results(1) }
      
      it 'for a new instance, the inspect method shows bare contents' do
        expect(described_class.new.inspect).to match %r{#<JrGoogleData::Row:0x(\p{XDigit}+)>}
      end

      it 'for a found instance, the inspect method shows row contents' do
        regex = %r|.+Row:.+ title: .+, content: \{ (\w+: \w+\s\s){1,16}\} >|
        expect(instance.inspect).to match regex
      end

      it 'a found instance, returns a Ruby Hash of row data' do
        hash = instance.to_hash
        expect(hash).to be_a(Hash)
        expect(hash.keys).to eq(['id', 'name', 'age', 'colour', 'size'])
        expect(hash.values).to eq(['1', 'Foo', '23', 'red', 'L'])
      end

      

    end
  end
end
