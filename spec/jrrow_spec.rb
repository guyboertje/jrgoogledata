require 'spec_helper'

module JrGoogleData
  describe Row do

    setup_worksheet_instance()

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
      let(:row)  do
        qry = Instances.worksheet.new_list_query.with_start_index(1).with_max_results(1) 
        Instances.worksheet.fetch_rows(qry).first
      end

      it 'for a new instance, the inspect method shows bare contents' do
        expect(described_class.new.inspect).to match %r{#<JrGoogleData::Row:0x(\p{XDigit}+)>}
      end

      it 'for a found instance, the inspect method shows row contents' do
        regex = %r|.+Row:.+ title: .+, content: \{ (\w+: \w+\s\s){1,16}\} >|
        expect(row.inspect).to match regex
      end

      it 'a found instance, returns a Ruby Hash of row data' do
        hash = row.to_hash
        expect(hash).to be_a(Hash)
        expect(hash.keys).to eq(['id', 'name', 'age', 'colour', 'size'])
        expect(hash.values).to eq(['1', 'Foo', '23', 'red', 'L'])
      end

      it 'updates one cell of a Row' do
        qry = Instances.worksheet.new_list_query.with_spreadsheet_query('id = 2').with_max_results(1)
        row = Instances.worksheet.fetch_rows(qry).last
        original = row.to_hash
        previous_colour = original['colour']
        new_colour = previous_colour == 'blue' ? 'black' : 'blue'
        row.modify('colour', new_colour)
        row.update
        expected = original.merge('colour' => new_colour)
        updated_row = Instances.worksheet.fetch_rows(qry).last.to_hash
        expect(updated_row).to eq(expected)
        expect(updated_row).not_to eq(original)
      end

      it 'updates all cells with a hash' do
        qry = Instances.worksheet.new_list_query.with_spreadsheet_query('id = 2').with_max_results(1)
        row = Instances.worksheet.fetch_rows(qry).last
        original = row.to_hash
        _name = original['name'] == 'Bar' ? 'Jazz' : 'Bar'
        _age = original['age'] == '83' ? '38' : '83'
        _colour = original['colour'] == 'blue' ? 'black' : 'blue'
        _size = original['size'] == 'S' ? 'XL' : 'S'
        _values = {'id'=>'2', 'name' => _name, 'age' => _age, 'colour' => _colour, 'size' => _size}
        row.merge(_values)
        row.update
        updated_row = Instances.worksheet.fetch_rows(qry).last.to_hash
        expect(updated_row).to eq(_values)
      end
    end
  end
end
