require 'spec_helper'

module JrGoogleData
  describe Worksheet do

    setup_worksheet_instance()

    context 'its public api' do
      let(:instance) { described_class.new }

      it 'has a constructor that takes no arguments' do
        expect{described_class.new}.not_to raise_error
      end

      it 'has an inspect method' do
        expect(instance).to respond_to(:inspect)
      end

      it 'has a new_list_query method' do
        expect(instance).to respond_to(:new_list_query)
      end

      it 'has a fetch_rows method' do
        expect(instance).to respond_to(:fetch_rows)
      end

      it 'has an add_row method' do
        expect(instance).to respond_to(:add_row)
      end

      it 'has an add_rows method' do
        expect(instance).to respond_to(:add_rows)
      end

      it 'has an new_row method' do
        expect(instance).to respond_to(:new_row)
      end

      it 'has an new_rows method' do
        expect(instance).to respond_to(:new_rows)
      end
    end

    context 'an instance' do
      it 'for a new instance, the inspect method shows bare contents' do
        expect(described_class.new.inspect).to match %r{#<JrGoogleData::Worksheet:0x(\p{XDigit}+)>}
      end

      it 'for a found instance, the inspect method shows url contents' do
        regex = %r{.+Worksheet:.+/feeds/cells.+/feeds/list.+, columns\: \["id", "name", "age", "colour", "size"\] >}
        actual = Instances.worksheet.inspect
        expect(actual).to match regex
      end

      it 'a found instance, returns a ListQuery object' do
        expect(Instances.worksheet.new_list_query).to be_a(ListQuery)
      end

      it 'given a loaded ListQuery object, it gets some rows' do
        qry = Instances.worksheet.new_list_query.with_start_index(1).with_max_results(2)
        results = Instances.worksheet.fetch_rows(qry)
        expect(results.size).to eq(2)
      end

      it 'given a new row, adds it to the end of the sheet' do
        qry = Instances.worksheet.new_list_query.with_start_index(1).with_max_results(500)
        last_row = Instances.worksheet.fetch_rows(qry).last.to_hash
        next_id = last_row.fetch('id', 1).to_i.next
        row = Instances.worksheet.new_row
        expected = {'id' => next_id.to_s, 'name' => 'Bazz', 'age' => '42', 'colour' => 'pink', 'size' => 'S'}
        row.merge(expected)
        Instances.worksheet.add_row(row)
        qry = Instances.worksheet.new_list_query.with_start_index(next_id).with_max_results(1)
        expect(Instances.worksheet.fetch_rows(qry).last.to_hash).to eq(expected)
      end
    end
  end
end
