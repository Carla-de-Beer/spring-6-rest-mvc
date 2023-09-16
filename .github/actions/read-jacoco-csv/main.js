'use strict';

const core = require('@actions/core');
const fs = require('fs');
const {parse} = require('csv-parse');

const main = async () => {
	let lineCovered = 0;
	let lineMissed = 0;
	const filepath = core.getInput('filepath');
	const minCoverageRequired = core.getInput('min-coverage-required');
	const emojiCheck = '✅';
	const emojiX = '❌';

	let summaryTable = [];

	summaryTable.push([
		{data: 'Package', header: true},
		{data: 'Class', header: true},
		{data: 'Instruction Missed', header: true},
		{data: 'Instruction Covered', header: true},
		{data: 'Percentage Covered', header: true},
		{data: 'Result', header: true}
	]);

	fs.createReadStream(filepath)
		.pipe(parse({delimiter: ',', from_line: 2}))
		.on('data', (row) => {
			lineMissed += Number(row[3]);
			lineCovered += Number(row[4]);

			if (lineCovered !== 0) {
				const percentageCovered = Math.floor(((lineCovered - lineMissed) / lineCovered) * 100);

				let emoji = emojiX;
				if (percentageCovered >= 90) {
					emoji = emojiCheck;
				}

				summaryTable.push([`${row[1]}`, `${row[2]}`, `${row[3]}`, `${row[4]}`, `${percentageCovered}%`, `${emoji}`]);
			} else {
				summaryTable.push([`${row[1]}`, `${row[2]}`, `${row[3]}`, `${row[4]}`, `-`, '❌']);
			}

		}).on('end', function () {
		let total = 0;
		let conclusion = `${emojiX} Minimum code coverage of ${minCoverageRequired}% not reached`;

		if (lineCovered !== 0) {
			total = Math.floor(((lineCovered - lineMissed) / lineCovered) * 100);
			if (total >= minCoverageRequired) {
				conclusion = `${emojiCheck} Minimum code coverage of ${minCoverageRequired}% has been reached`;
			} else {
				conclusion = `${emojiX} Minimum code coverage of ${minCoverageRequired}% has not been reached`;
				core.setFailed(`Minimum code coverage of ${minCoverageRequired}% has not been reached`);
			}
		}

		let totalAsString = total.toString();
		if (total === 0) {
			totalAsString = '-';
		}

		core.summary
			.addHeading('JaCoCo Code Coverage Summary')
			.addTable(summaryTable)
			.addRaw('\n')
			.addRaw(`Total coverage: ${totalAsString}%`)
			.addRaw('\n')
			.addRaw(conclusion, true)
			.write();
	});
};

main().catch(err => core.setFailed(err.message));
